package Dust;

import java.io.IOException;
import java.io.OutputStream;
 
import javax.swing.JTextArea;
 
/**
 * This class extends from OutputStream to redirect output to a JTextArrea
 * http://www.codejava.net/java-se/swing/redirect-standard-output-streams-to-jtextarea
 * 
 * @author www.codejava.net
 *
 */
public class MenuOutputStream extends OutputStream {
    private JTextArea textArea;
     
    public MenuOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }
     
    @Override
    public void write(int b) throws IOException {
        // redirects data to the text area
        textArea.append(String.valueOf((char)b));
        // scrolls the text area to the end of data
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
