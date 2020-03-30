package learn.classloader;

import java.io.*;

/**
 * 自定义类加载器
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: CustomClassLoader
 * @author: Glorze
 * @since: 2020/3/30 22:07
 */
public class CustomClassLoader extends ClassLoader {

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] result = getClassFromCustomPath(name);
            if (null == result) {
                throw new FileNotFoundException();
            } else {
                return defineClass(name, result, 0, result.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ClassNotFoundException(name);
    }

    private byte[] getClassFromCustomPath(String name) throws IOException {
        // 文件读取
        FileInputStream fis = null;
        byte[] data = {};
        try {
            String fileName = "/XX/learn/classloader/One.class";
            fis = new FileInputStream(fileName);
            int len = fis.available();
            data = new byte[len];
            fis.read(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static void main(String[] args) {
        CustomClassLoader customClassLoader = new CustomClassLoader();
        try {
            Class<?> clazz = Class.forName("learn.classloader.One", true, customClassLoader);
            Object object = clazz.newInstance();
            System.out.println(object.getClass().getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
