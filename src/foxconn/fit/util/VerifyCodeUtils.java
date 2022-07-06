package foxconn.fit.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class VerifyCodeUtils {
    private int width;
    private int height;
    private static Random random = new Random();
    private String randomStr = "";
    private HttpServletRequest request;
    private HttpServletResponse response;

    public VerifyCodeUtils(int width, int height, HttpServletRequest request, HttpServletResponse response) {
        this.width = width;
        this.height = height;
        this.request = request;
        this.response = response;
        this.setRandomString();
    }

    // 随机字符字典（不包括0，1，O，I难辨认的字符）
    public final static char[] chars = { '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
            'K', 'M', 'L', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    // 产生4位随机数·字符
    public void setRandomString() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            buffer.append(chars[random.nextInt(chars.length)]);
        }
        randomStr = buffer.toString();
    }
    
    // 取得随机字符串
    public String getRandomString() {
        HttpSession s = request.getSession(true);
        s.setAttribute("verifyCode", randomStr);
        s.setAttribute("verifyCodeTime", System.currentTimeMillis());
        return randomStr;
    }

    // 获得产生的图像
    public Image getImage() throws Exception {
        response.setContentType("image/jpeg");
        // 设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        BufferedImage validate = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = validate.getGraphics();
        // 设定背景色
        g.setColor(new Color(227, 238, 237));
        g.fillRect(0, 0, width, height);

        // 随机产生80条干扰线，使图象中的认证码不易被其它程序探测到
        g.setColor(new Color(149, 181, 185));
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(5);
            int yl = random.nextInt(5);
            g.drawLine(x, y, x + xl, y + yl);
        }

        g.setColor(new Color(15, 155, 171));
        for (int i = 0; i < 30; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(5);
            int yl = random.nextInt(5);
            g.drawLine(x, y, x + xl, y + yl);
        }
        // 设置字体和字符颜色
        g.setColor(new Color(15, 155, 171));
        g.setFont(new Font("Times New Roman", Font.PLAIN, height));
        // 画出字符验证码
        g.drawString(getRandomString(), width / 20, height - height / 6);
        // 图像生成
        g.dispose();
        ImageIO.write(validate, "JPEG", response.getOutputStream());
        return validate;
    }
}
