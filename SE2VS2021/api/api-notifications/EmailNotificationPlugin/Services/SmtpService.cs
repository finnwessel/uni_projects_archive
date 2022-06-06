using System.Net.Mail;

namespace EmailNotificationPlugin.Services;

public class SmtpService
{
    private readonly SmtpClient _smtpClient;
    private readonly string _mailFrom;
    public SmtpService(string mailFrom, string password, string host = "smtp.gmail.com", int port = 587)
    {
        _smtpClient = new SmtpClient();
        _smtpClient.DeliveryMethod = SmtpDeliveryMethod.Network;
        _smtpClient.EnableSsl = true;
        _smtpClient.Host = host;
        _smtpClient.Port = port;
        _mailFrom = mailFrom;
        _smtpClient.UseDefaultCredentials = false;
        _smtpClient.Credentials = new System.Net.NetworkCredential(_mailFrom, password); 
    }

    public int SendMail(string mailTo, string subject, string htmlBody)
    {
        var msg = new MailMessage();
        msg.From = new MailAddress(_mailFrom);
        msg.To.Add(new MailAddress(mailTo));

        msg.Subject = subject;
        msg.IsBodyHtml = false;
        msg.Body = string.Format(htmlBody);
        try
        {
            _smtpClient.Send(msg);
            //lblMsg.Text = "Your message has been successfully sent.";
            return 1;
        }
        catch
        {
            // Exception ex
            //lblMsg.ForeColor = Color.Red;
            //lblMsg.Text = "Error occured while sending your message." + ex.Message;
            return -1;
        }
    }
}