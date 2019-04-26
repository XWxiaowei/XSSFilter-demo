package com.jay.xss;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author xiang.wei
 * @create 2019/4/25 20:39
 */
public class HTMLStringFilterTest {

    /**
     * 正常情况
     */
    @Test
    public void cleanSafeHtml() {
        String h = "<h1>Head</h1><table><tr><td>One<td>Two</td></tr></table>";
        String safeHtml = HTMLStringFilter.cleanSafeHtml(h);
        assertEquals("<h1>Head</h1>\n" +
                "<table>\n" +
                " <tbody>\n" +
                "  <tr>\n" +
                "   <td>One</td>\n" +
                "   <td>Two</td>\n" +
                "  </tr>\n" +
                " </tbody>\n" +
                "</table>", safeHtml);
    }

    @Test
    public void testDropComments() {
        String h = "<p>Hello<!-- no --></p>";
        String cleanHtml = HTMLStringFilter.cleanSafeHtml(h);
        assertEquals("<p>Hello</p>", cleanHtml);
    }

    @Test
    public void testDropXmlProc() {
        String h = "<?import namespace=\"xss\"><p>Hello</p>";
        String cleanHtml = HTMLStringFilter.cleanSafeHtml(h);
        assertEquals("<p>Hello</p>", cleanHtml);
    }

    @Test
    public void illegalImg() {
        try {
            String h = "<div><p class=foo><a href='http://evil.com'>Hello <b id=bar>there</b>!</a><img src='https://baidu.com/FileUpload//shop/website/20171101104051422/20171101104051422_MID_20171101104051.jpg'/></div>";
            String cleanHtml = HTMLStringFilter.cleanSafeHtml(h);
        } catch (RuntimeException e) {
            assertEquals("图片域名非法，不能保存", e.getMessage());
        }

    }

    @Test
    public void legalImg() {
        String h = "<div><p class=foo><a href='http://evil.com'>Hello <b id=bar>there</b>!</a><img src='https://www.jss.com.cn/FileUpload//shop/website/20171101104051422/20171101104051422_MID_20171101104051.jpg'/></div>";
        String cleanHtml = HTMLStringFilter.cleanSafeHtml(h);
        assertEquals("<div>\n" +
                " <p><a href=\"http://evil.com\">Hello <b>there</b>!</a><img src=\"https://www.jss.com.cn/FileUpload//shop/website/20171101104051422/20171101104051422_MID_20171101104051.jpg\"></p>\n" +
                "</div>", cleanHtml);

    }

    @Test
    public void testDropScript() {
        String h = "<SCRIPT SRC=//ha.ckers.org/.j><SCRIPT>alert(/XSS/.source)</SCRIPT>";
        String cleanHtml = HTMLStringFilter.cleanSafeHtml(h);
        assertEquals("", cleanHtml);
    }

    @Test
    public void testDropImageScript() {
        String h = "<IMG SRC=\"javascript:alert('XSS')\">";
        String cleanHtml = HTMLStringFilter.cleanSafeHtml(h);
        assertEquals("<img>", cleanHtml);
    }

    @Test
    public void testCleanJavascriptHref() {
        String h = "<A HREF=\"javascript:document.location='http://www.google.com/'\">XSS</A>";
        String cleanHtml = HTMLStringFilter.cleanSafeHtml(h);
        assertEquals("<a>XSS</a>", cleanHtml);
    }

    @Test
    public void testDropsUnknownTags() {
        String h = "<p><custom foo=true>Test</custom></p>";
        String cleanHtml = HTMLStringFilter.cleanSafeHtml(h);
        assertEquals("<p>Test</p>", cleanHtml);
    }

    @Test
    public void testHandlesEmptyAttributes() {
        String h = "<img alt=\"\" src= unknown=''>";
        String cleanHtml = Jsoup.clean(h, Whitelist.basicWithImages());
        assertEquals("<img alt=\"\">", cleanHtml);
    }

}