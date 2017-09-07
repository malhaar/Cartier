package xyz.vopen.cartier.commons.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 邮件管理器(支持邮件队列,定时邮件,黑白名单,失败重试降级机制等等)
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 09/05/2017.
 */
public final class CartierMailManager {

    private static Logger logger = LoggerFactory.getLogger(CartierMailManager.class);

    //
    private CartierMailManager () {
    }

    private static class InstanceHolder {
        private static CartierMailManager INSTANCE = new CartierMailManager();
    }

    public static CartierMailManager getInstance () {
        return InstanceHolder.INSTANCE;
    }


    /**
     * 初始化邮件管理器
     */
    public void init () {
        // 初始化队列, 初始化定时器 ,持久化线程 , 记忆线程
        
    }


    /**
     * 销毁
     */
    public void destory () {
        // 释放各种资源
        
    }
    
    
    


    // test
    public static void main (String[] args) {

    }

}
