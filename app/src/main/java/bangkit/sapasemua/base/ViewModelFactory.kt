package amirlabs.sapasemua.base

import amirlabs.sapasemua.data.api.NetworkConfig
import amirlabs.sapasemua.data.local.AppDatabase
import amirlabs.sapasemua.data.repo.MainRepositoryImpl
import amirlabs.sapasemua.ui.auth.login.LoginViewModel
import amirlabs.sapasemua.ui.auth.register.RegisterViewModel
import amirlabs.sapasemua.ui.menu.forum.ForumViewModel
import amirlabs.sapasemua.ui.menu.forum.create_discussion.CreateDiscussionViewModel
import amirlabs.sapasemua.ui.menu.forum.detail.ForumDetailViewModel
import amirlabs.sapasemua.ui.menu.forum.list.ForumListViewModel
import amirlabs.sapasemua.ui.menu.home.HomeViewModel
import amirlabs.sapasemua.ui.menu.module.ModuleViewModel
import amirlabs.sapasemua.ui.menu.module.add_module.AddModuleViewModel
import amirlabs.sapasemua.ui.menu.module.add_quiz.AddQuizViewModel
import amirlabs.sapasemua.ui.menu.module.add_submodule.AddSubmoduleViewModel
import amirlabs.sapasemua.ui.menu.module.edit_module.EditModuleViewModel
import amirlabs.sapasemua.ui.menu.module.edit_quiz.EditQuizViewModel
import amirlabs.sapasemua.ui.menu.module.lesson.LessonViewModel
import amirlabs.sapasemua.ui.menu.module.list_quiz.ListQuizViewModel
import amirlabs.sapasemua.ui.menu.module.quiz.QuizViewModel
import amirlabs.sapasemua.ui.menu.module.submodule.SubModuleViewModel
import amirlabs.sapasemua.ui.menu.profile.ProfileViewModel
import amirlabs.sapasemua.ui.menu.translate.TranslateViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.Factory {
    private val db = AppDatabase.getAppDatabase()
    private val api = NetworkConfig.apiService
    private val mainRepo = MainRepositoryImpl(db.videoDao(), api)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DevViewModel::class.java) -> DevViewModel() as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(mainRepo) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(mainRepo) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel() as T
            modelClass.isAssignableFrom(ModuleViewModel::class.java) -> ModuleViewModel(mainRepo) as T
            modelClass.isAssignableFrom(AddModuleViewModel::class.java) -> AddModuleViewModel(mainRepo) as T
            modelClass.isAssignableFrom(TranslateViewModel::class.java) -> TranslateViewModel(mainRepo) as T
            modelClass.isAssignableFrom(SubModuleViewModel::class.java) -> SubModuleViewModel(mainRepo) as T
            modelClass.isAssignableFrom(LessonViewModel::class.java) -> LessonViewModel(mainRepo) as T
            modelClass.isAssignableFrom(ListQuizViewModel::class.java) -> ListQuizViewModel(mainRepo) as T
            modelClass.isAssignableFrom(AddQuizViewModel::class.java) -> AddQuizViewModel(mainRepo) as T
            modelClass.isAssignableFrom(QuizViewModel::class.java) -> QuizViewModel(mainRepo) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(mainRepo) as T
            modelClass.isAssignableFrom(ForumViewModel::class.java) -> ForumViewModel(mainRepo) as T
            modelClass.isAssignableFrom(ForumListViewModel::class.java) -> ForumListViewModel(mainRepo) as T
            modelClass.isAssignableFrom(ForumDetailViewModel::class.java) -> ForumDetailViewModel(mainRepo) as T
            modelClass.isAssignableFrom(CreateDiscussionViewModel::class.java) -> CreateDiscussionViewModel(mainRepo) as T
            modelClass.isAssignableFrom(AddSubmoduleViewModel::class.java) -> AddSubmoduleViewModel(mainRepo) as T
            modelClass.isAssignableFrom(EditModuleViewModel::class.java) -> EditModuleViewModel(mainRepo) as T
            modelClass.isAssignableFrom(EditQuizViewModel::class.java) -> EditQuizViewModel(mainRepo) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        @JvmStatic
        val viewModelFactory = ViewModelFactory()
    }
}