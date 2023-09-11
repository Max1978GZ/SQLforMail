 /*
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
/**
 * Febrero del 2014
 *
 * @author Máximo Coejo Cores mcoejo@gmail.com Adaptacion del codigo de email de
 * http://www.chuidiang.com/java/herramientas/javamail/empezar-javamail.php
 *
 */
package modelo;

import java.util.Properties;
import java.util.Random;
import javax.mail.Folder;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Ejemplo de envio de correo simple con JavaMail
 *
 * @author
 *
 */
public class MAIL {

    public static String emailDestino = "micorreo@gmail.com";

    /**
     * Metodo que encia correo al la direccion indicada en la variable global
     * emailDestino
     *
     * @param sujeto
     * @param body
     * @param user
     * @param passw
     */
    public static void EnviarMail(String sujeto, String body, String user, String passw) {
        System.out.println(sujeto + " - " + body);
        try {

            // Propiedades de la conexión
            Properties props = new Properties();
            props.setProperty("mail.smtp.host", "smtp.gmail.com");
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.port", "587");
            props.setProperty("mail.smtp.user", user);
            props.setProperty("mail.smtp.auth", "true");

            // Preparamos la sesion
            Session session = Session.getDefaultInstance(props);

            // Construimos el mensaje
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MAIL.emailDestino));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(MAIL.emailDestino));
            Random a = new Random();
            message.setSubject(sujeto);
            message.setText(body);

            // Lo enviamos. 
            Transport t = session.getTransport("smtp");
            t.connect(user, passw);
            t.sendMessage(message, message.getAllRecipients());

            // Cierre.
            t.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * metodo que recibe los emails que envia el MailMonitorSQL
     *
     * @param user
     * @param passw
     * @return
     */
    public static modelo.Remitente RecibirMail(String user, String passw) {

        modelo.Remitente re = new modelo.Remitente();
        // Se obtiene la Session
        Properties prop = new Properties();
        prop.setProperty("mail.pop3.starttls.enable", "false");
        prop.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.setProperty("mail.pop3.socketFactory.fallback", "false");
        prop.setProperty("mail.pop3.port", "995");
        prop.setProperty("mail.pop3.socketFactory.port", "995");
        Session sesion = Session.getInstance(prop);
        // sesion.setDebug(true);

        try {

            Store store = sesion.getStore("pop3");
            store.connect("pop.gmail.com", user, passw);
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            // Se obtienen los mensajes.
            Message[] mensajes = folder.getMessages();
            if (0 == mensajes.length) {
                System.out.println("No hay mensajes");
                return re;
            }

            re = new modelo.Remitente(mensajes[mensajes.length - 1].getFrom()[0].toString(),
                    mensajes[mensajes.length - 1].getSubject());

            re.setBody(mensajes[mensajes.length - 1].getContent().toString());

            folder.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return re;
    }

}
