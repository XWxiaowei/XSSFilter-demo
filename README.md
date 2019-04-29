# XSSFilter-demo
## 摘要
本次处理富文本框XSS注入，后端处理采用的是jsoup框架。GitHub 中的地址：https://jsoup.org/
其是Java世界的一款HTML解析工具，它支持用CSS Selector方式选择DOM元素，也可过滤HTML文本，防止XSS攻击。
## 如何使用
### 1. 引入依赖
```xml
  <dependency>
      <!-- jsoup HTML parser library @ https://jsoup.org/ -->
      <groupId>org.jsoup</groupId>
      <artifactId>jsoup</artifactId>
      <version>1.11.2</version>
    </dependency>
```
### 2. 将HTMLStringFilter 加入到项目中
本demo中使用的白名单是jsoup 框架自带的白名单，其核心逻辑如下；
```java
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

```
### 3. 图片地址处理
如果需要对图片进行校验的话，则需要配置WhiteImgUrl.properties

``` #是否开启图片校验
image.valid.open=true
#合法的图片域名(每个域名都要完全匹配,一个匹配项都没有找到，则提示异常)
legalImage.url=https://yyy.com.cn;https://www.xxx.com.cn
```
### 4. 使用
**将需要过滤的存入富文本字段传入cleanSafeHtml方法即可。**


