package com.harfield.crawler.apps;

import com.jfinal.core.JFinal;

/**
 * @author harfield
 */
public class StartWebUI {
    public static void main(String[] args) {
        JFinal.start("src/main/webapp",80,"/");
    }
}
