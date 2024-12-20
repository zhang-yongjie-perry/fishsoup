package com.fishsoup.fishweb.controller;

import com.fishsoup.fishweb.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * ueditor 需要前后端进行配置, 且缺少java第三方库的支持, 本项目采用wangeditor
 */
@Slf4j
@Deprecated
@RestController
@RequestMapping("/editor")
public class EditorController {

    @GetMapping("/config")
    public String getConfig() throws BusinessException {
        StringBuilder configOptions = new StringBuilder();
        String resourceName = "config/editorConfig.json";
        // 使用类加载器读取资源文件
        try (InputStream inputStream = ClassUtils.getDefaultClassLoader().getResourceAsStream(resourceName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                System.out.println("资源文件未找到: " + resourceName);
                log.error("富文本器配置文件未找到: {}", resourceName);
                throw new BusinessException("富文本器配置文件未找到");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                configOptions.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configOptions.toString();
    }
}
