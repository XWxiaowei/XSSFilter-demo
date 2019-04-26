package com.jay.xss;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiang.wei
 * @create 2019/4/25 16:55
 */
public class HTMLStringFilter {
    /**
     * 图片正则表达式处理
     */
    private static String REGEX_IMG = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
    /**
     * 图片正则表达式处理
     */
    private static String SRC_IMG = "src\\s*=\\s*\"?(.*?)(\"|>|\\s+)";

    /**
     * 默认校验图片
     */
    private static boolean ISVALID_IMAGE = true;
    /**
     * 合法的图片域名
     */
    private static List<String> LEGAL_IMAGEURLS = new ArrayList<>();

    //    静态代码块主要是预加载图片域名白名单设置
    static {
        String resource = "WhiteImgUrl.properties";
        InputStream is = ClassLoader.getSystemResourceAsStream(resource);
        try {
            if (is != null) {
                Properties prop = new Properties();
                prop.load(is);
                String isValidImageStr = prop.getProperty("image.valid.open");
                if (StringUtils.isNotBlank(isValidImageStr) &&
                        ("true".equals(isValidImageStr) || "false".equals(isValidImageStr))) {
                    ISVALID_IMAGE = Boolean.parseBoolean(isValidImageStr);
                }
                String imageUrls = prop.getProperty("legalImage.url");
                if (StringUtils.isNotBlank(imageUrls)) {
                    LEGAL_IMAGEURLS = Arrays.asList(imageUrls.split(";"));
                }
            }
        } catch (IOException e) {
            System.out.println("------》配置文件加载失败={}"+e.getMessage());
        }
    }

    public static void main(String[] args) {
//        String originHtml = "<script></script><div>&ddd</div>< div onclick=''>" +
//                "<img src='http://2111s.jsp'/>ddd</div><input>131312</input><img></img><a onmousemove=’do something here’> \n";
        String originHtml = "<div><p class=foo><a href='http://evil.com'>Hello <b id=bar>there</b>!</a><img src='https://www.jss.com.cn/FileUpload//shop/website/20171101104051422/20171101104051422_MID_20171101104051.jpg'/></div>";
        System.out.println("原始的originHtml={}" + originHtml);
        String safeHtml = cleanSafeHtml(originHtml);
        System.out.println("安全的safeHtml={}" + safeHtml);

    }

    /**
     * 采用jsoup白名单方式过滤非法的html字符。
     * 原理：
     * 1.首先通过白名单过滤掉非法的html标签，即只允许输出白名单内的标签
     * 2.对特殊的属性（主要是style）用正则过滤，只允许安全的属性值存在
     *
     * @param originHtml 原始的html片段（用户通过富文本编辑器提交的html代码）
     * @return 过滤后的安全的html片段
     */
    public static String cleanSafeHtml(String originHtml) {
        Whitelist whitelist = Whitelist.relaxed();
        //获得安全HTML，消除xss隐患
        String safeHtml = Jsoup.clean(originHtml, whitelist);
        if (ISVALID_IMAGE) {
            getImgSrc(safeHtml);
        }
        return safeHtml;
    }



    /**
     * 获取图片地址，校验图片
     *
     * @param htmlStr
     * @return
     */
    public static List<String> getImgSrc(String htmlStr) {
        String img = "";
        Pattern p_image;
        Matcher m_image;
        List<String> pics = new ArrayList<String>();
        p_image = Pattern.compile(REGEX_IMG, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            img = img + "," + m_image.group();
            Matcher m = Pattern.compile(SRC_IMG).matcher(img);
            while (m.find()) {
                String url = m.group(1);
                String imgUrlPre = null;
                boolean match = false;
//             判断图片域名是否合法
                String[] imgUrlStr = url.split("/");
                if (imgUrlStr.length >= 3) {
                    imgUrlPre = imgUrlStr[0] + imgUrlStr[1] + "//" + imgUrlStr[2];
                }
                for (String imageurl : LEGAL_IMAGEURLS) {
                    if (imageurl.equals(imgUrlPre)) {
                        match = true;
                    }
                }
                if (!match) {
                    throw new ImageValidException("图片域名非法，不能保存");
                }
                pics.add(url);
            }
        }
        return pics;
    }

    /**
     *  过滤前替换
     * @param originHtml
     * @return
     */
    private String preHtmlDeal(String originHtml) {

        return null;
    }

}
