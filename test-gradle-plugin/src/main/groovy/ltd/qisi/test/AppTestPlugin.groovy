package ltd.qisi.test

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import ltd.qisi.test.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project


class AppTestPlugin implements Plugin<Project> {
    private static final String EXT_NAME = "appTest"

    @Override
    void apply(Project project) {
        Logger.make(project)
        if (project.plugins.hasPlugin(AppPlugin)) {
            project.extensions.create(EXT_NAME, AppTestExtension)
            AppExtension appExtension = project.extensions.getByType(AppExtension)
            AppTestTransform transform = new AppTestTransform(project)
            appExtension.registerTransform(transform)
            project.afterEvaluate {
                def extension = project.extensions.findByName(EXT_NAME) as AppTestExtension
                Logger.enabled = extension.logEnabled
                transform.extension = extension
            }
        }
    }
}