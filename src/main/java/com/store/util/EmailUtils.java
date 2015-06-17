package com.store.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * 邮件发送工具
 * 
 * @author zhouzh
 */
public class EmailUtils {

	private static final String SENDER = "****服务";

	private static final String SENDER_MAIL = "service_124@163.com";

	protected static JavaMailSenderImpl mailSender;

	static {
		mailSender = new JavaMailSenderImpl();
		Properties javaMailProperties = new Properties();
		javaMailProperties.setProperty("mail.smtp.port", "25");
		javaMailProperties.setProperty("mail.smtp.auth", "true");
		javaMailProperties.setProperty("mail.smtp.connectiontimeout", "1800");
		javaMailProperties.setProperty("mail.smtp.timeout", "6000");
		mailSender.setHost("smtp.163.com");
		mailSender.setJavaMailProperties(javaMailProperties);
		mailSender.setUsername("service_124@163.com");
		mailSender.setPassword("f80132ca1ea5536f");//已加密
	}

	/**
	 * 发送简单邮件
	 *
	 * @param emails
	 *            目标Email地址
	 * @param title
	 *            邮件标题
	 * @param context
	 *            邮件内容
	 * @param isHTML
	 *            如果为true则邮件内容以HTML文本格式发送否则以普通文本格式发送
	 * @param encoding
	 *            邮件内容编码
	 * @return 发送成功返回true否则返回false
	 */
	public static void sendSimpleEmail(String emails, String title,
			String context, boolean isHTML, String encoding) {
		MimeMessage mail = mailSender.createMimeMessage();
		try {
			if (null != emails && !"".equals(emails.trim())) {
				MimeMessageHelper maileHelper = new MimeMessageHelper(mail,
						true, encoding);
				maileHelper.setTo(emails.trim());
				maileHelper.setFrom(new InternetAddress(EmailUtils.SENDER_MAIL,
						EmailUtils.SENDER));
				maileHelper.setSubject(title);
				maileHelper.setText(context, isHTML);
				mailSender.send(mail);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 异步发送邮件
	 *
	 * @param emails
	 *            目标邮件地址
	 * @param title
	 *            邮件标题
	 * @param context
	 *            邮件内容
	 * @param isHTML
	 *            如果为true则邮件内容以HTML文本格式发送否则以普通文本格式发送
	 * @param encoding
	 *            邮件内容编码
	 */
	public static void sendMail(final String[] emails, final String title,
			final String context, final boolean isHTML, final String encoding) {
		if (emails != null && emails.length > 0) {
			Runnable thread = new Runnable() {

				public void run() {

					try {

						List<String> ms = new ArrayList<String>();

						for (String emailAddr : emails) {
							if (null != emailAddr && !"".equals(emailAddr)) {
								ms.add(emailAddr);
							}
						}

						MimeMessage mail = mailSender.createMimeMessage();
						MimeMessageHelper maileHelper = new MimeMessageHelper(
								mail, true, encoding);

						maileHelper.setTo(ms.toArray(new String[ms.size()]));
						maileHelper.setFrom(new InternetAddress(
								EmailUtils.SENDER_MAIL, EmailUtils.SENDER));
						maileHelper.setSubject(title);
						maileHelper.setText(context, isHTML);
						mailSender.send(mail);

					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			};
			new Thread(thread).start();
		}
	}

	/**
	 * 异步发邮件
	 *
	 * @param emails
	 *            目标邮件地址
	 * @param title
	 *            邮件标题
	 * @param context
	 *            邮件内容
	 * @param isHTML
	 *            如果为true则邮件内容以HTML文本格式发送否则以普通文本格式发送
	 * @param encoding
	 *            邮件内容编码
	 * @param files
	 *            文件附件
	 */
	public static void sendMail(final String[] emails, final String title,
			final String context, final boolean isHTML, final String encoding,
			final List<File> files) {

		Runnable thread = new Runnable() {

			public void run() {

				try {

					List<String> ms = new ArrayList<String>();

					for (String emailAddr : emails) {
						if (null != emailAddr && !"".equals(emailAddr)) {
							ms.add(emailAddr);
						}
					}

					MimeMessage mail = mailSender.createMimeMessage();
					MimeMessageHelper maileHelper = new MimeMessageHelper(mail,
							true, encoding);

					for (int i = 0; i < files.size(); i++) {
						FileSystemResource file = new FileSystemResource(
								files.get(i));
						maileHelper.addInline(file.getFilename(), file);
						maileHelper.addAttachment(file.getFilename(), file);
					}
					maileHelper.setTo(ms.toArray(new String[ms.size()]));
					maileHelper.setFrom(new InternetAddress(
							EmailUtils.SENDER_MAIL, EmailUtils.SENDER));
					maileHelper.setSubject(title);
					maileHelper.setText(context, isHTML);
					mailSender.send(mail);

				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		};
		new Thread(thread).start();
	}

}
