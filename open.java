import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

class JarInCurrentJvm {
    public static void main(String[] args) throws Exception {
        // 1. 获取工作目录下的目标 JAR 文件
        String workDir = System.getProperty("user.dir");
        File jarFile = new File(workDir, "target-tool.jar");

        if (!jarFile.exists()) {
            throw new RuntimeException("目标 JAR 不存在: " + jarFile.getAbsolutePath());
        }

        // 2. 创建 URLClassLoader 加载该 JAR
        URL jarUrl = jarFile.toURI().toURL();
        // 父类加载器使用当前线程的上下文类加载器，确保能访问主程序的类
        URLClassLoader classLoader = new URLClassLoader(
            new URL[]{jarUrl},
            Thread.currentThread().getContextClassLoader()
        );

        // 3. 通过反射加载目标 JAR 的主类（需要你知道主类的全限定名）
        //    如果不知道主类名，可以先用 JarFile API 读取 MANIFEST.MF 获取 Main-Class
        Class<?> mainClass = classLoader.loadClass("com.example.TargetMain");

        // 4. 获取 main 方法并调用
        Method mainMethod = mainClass.getMethod("main", String[].class);
        // main 方法是 static 的，所以 invoke 第一个参数传 null
        // 第二个参数是要传给目标 main 方法的 args，没有就传空数组
        mainMethod.invoke(null, (Object) new String[]{});

        // 5. 使用完毕后关闭类加载器，释放 JAR 文件句柄（JDK 7+）
        classLoader.close();
    }
}