package amirlabs.sapasemua.ui.menu.translate

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.data.model.HandCoordinate
import amirlabs.sapasemua.databinding.FragmentTranslateBinding
import amirlabs.sapasemua.utils.getViewModel
import amirlabs.sapasemua.utils.mediapipe.HandLandmarkerHelper
import android.util.Log
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TranslateFragment : DevFragment<FragmentTranslateBinding>(R.layout.fragment_translate),
    HandLandmarkerHelper.LandmarkerListener {
    override val vm: TranslateViewModel by getViewModel()
    private lateinit var handLandmarkerHelper: HandLandmarkerHelper
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT

    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundExecutor: ExecutorService
    override fun initData() {
        backgroundExecutor = Executors.newSingleThreadExecutor()
        backgroundExecutor.execute {
            handLandmarkerHelper = HandLandmarkerHelper(
                context = requireContext(),
                runningMode = RunningMode.LIVE_STREAM,
                minHandDetectionConfidence = vm.currentMinHandDetectionConfidence,
                minHandTrackingConfidence = vm.currentMinHandTrackingConfidence,
                minHandPresenceConfidence = vm.currentMinHandPresenceConfidence,
                maxNumHands = vm.currentMaxHands,
                currentDelegate = vm.currentDelegate,
                handLandmarkerHelperListener = this
            )
        }


    }

    override fun initUI() {
        binding.viewFinder.post {
            // Set up the camera and its use cases
            setUpCamera()
        }
    }

    override fun initAction() {
        backgroundExecutor.execute {
            if (handLandmarkerHelper.isClose()) {
                handLandmarkerHelper.setupHandLandmarker()
            }
        }
        vm.refreshConnection()
    }

    override fun initObserver() {
        vm.eventListener.observe(viewLifecycleOwner) {
            when (it) {
                is WebSocket.Event.OnMessageReceived -> {
                    if(it.message is Message.Text) {
                        binding.tvResult.text = (it.message as Message.Text).value
                    }
                }
                is WebSocket.Event.OnConnectionClosed -> {
                    Log.d("Translate", "onClosed")
                }
                is WebSocket.Event.OnConnectionFailed -> {
                    vm.refreshConnection()
                }
                else->{}
            }
        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun bindCameraUseCases() {

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(cameraFacing).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(backgroundExecutor) { image ->
                        detectHand(image)
                    }
                }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e("Translate", "Use case binding failed", exc)
        }
    }

    private fun detectHand(imageProxy: ImageProxy) {
        handLandmarkerHelper.detectLiveStream(
            imageProxy = imageProxy,
            isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
        )
    }

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
//        if (!PermissionsFragment.hasPermissions(requireContext())) {
//            Navigation.findNavController(
//                requireActivity(), R.id.fragment_container
//            ).navigate(R.id.action_camera_to_permissions)
//        }

        // Start the HandLandmarkerHelper again when users come back
        // to the foreground.
//        backgroundExecutor.execute {
//            if (handLandmarkerHelper.isClose()) {
//                handLandmarkerHelper.setupHandLandmarker()
//            }
//        }
    }

    override fun onPause() {
        super.onPause()
        if (this::handLandmarkerHelper.isInitialized) {
            vm.setMaxHands(handLandmarkerHelper.maxNumHands)
            vm.setMinHandDetectionConfidence(handLandmarkerHelper.minHandDetectionConfidence)
            vm.setMinHandTrackingConfidence(handLandmarkerHelper.minHandTrackingConfidence)
            vm.setMinHandPresenceConfidence(handLandmarkerHelper.minHandPresenceConfidence)
            vm.setDelegate(handLandmarkerHelper.currentDelegate)

            // Close the HandLandmarkerHelper and release resources
            backgroundExecutor.execute { handLandmarkerHelper.clearHandLandmarker() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Shut down our background executor
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )
    }

    override fun onError(error: String, errorCode: Int) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            if (errorCode == HandLandmarkerHelper.GPU_ERROR) {
//                binding.bottomSheetLayout.spinnerDelegate.setSelection(
//                    HandLandmarkerHelper.DELEGATE_CPU, false
//                )
            }
        }
    }

    override fun onResults(resultBundle: HandLandmarkerHelper.ResultBundle) {
        if (resultBundle.results.isNotEmpty()) {
            val landmarks = resultBundle.results.first().landmarks()
            if (landmarks.isNotEmpty()) {
                val body = Subscribe(
                    landmarks.first().map {
                        HandCoordinate(it.x(), it.y(), it.z())
                    }
                )
                vm.sendCoordinates(body)

            }

        }
        activity?.runOnUiThread {
            // Pass necessary information to OverlayView for drawing on the canvas
            binding.overlay.setResults(
                resultBundle.results.first(),
                resultBundle.inputImageHeight,
                resultBundle.inputImageWidth,
                RunningMode.LIVE_STREAM
            )

            // Force a redraw
            binding.overlay.invalidate()
        }
//        [ // container
//            [ // p1
//                [Category "Left" (displayName=Left score=0.9762654 index=0)],
//                [Category "Right" (displayName=Right score=0.93020505 index=1)]
//            ],
//            [ // p2
//                [Category "Left" (displayName=Left score=0.9762654 index=0)],
//                [Category "Right" (displayName=Right score=0.93020505 index=1)]
//            ]
//        ]
    }
}