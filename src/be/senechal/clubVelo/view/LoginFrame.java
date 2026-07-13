package be.senechal.clubVelo.view;

import java.awt.*;
import javax.swing.*;

import be.senechal.clubVelo.model.Manager;
import be.senechal.clubVelo.model.Member;
import be.senechal.clubVelo.model.Person;
import be.senechal.clubVelo.model.Treasurer;
import be.senechal.clubVelo.session.Session;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Connexion - Club Vélo");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        JPanel p1 = new JPanel();
        p1.add(new JLabel("Username :"));
        usernameField = new JTextField(15);
        p1.add(usernameField);

        JPanel p2 = new JPanel();
        p2.add(new JLabel("Password :"));
        passwordField = new JPasswordField(15);
        p2.add(passwordField);

        JButton loginBtn = new JButton("Se connecter");
        loginBtn.addActionListener(e -> attemptLogin());

        add(p1);
        add(p2);
        add(loginBtn);
        
        JButton signupBtn = new JButton("S'inscrire");
        signupBtn.addActionListener(e -> new CreateMemberFrame().setVisible(true));
        add(signupBtn);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        Person user = Person.loadPerson(username, password);

        if (user != null) {
            Session.login(user);
            JOptionPane.showMessageDialog(this, "Connexion réussie ! Bienvenue " + user.getFirstname());
            this.dispose();

            if (user instanceof Manager) {
                 new ManagerFrame().setVisible(true);
            }
            else if (user instanceof Treasurer) {
                 new TreasurerFrame().setVisible(true);
            }
            else if (user instanceof Member) {
                new HomeFrame().setVisible(true);
            }

        } else {
            JOptionPane.showMessageDialog(this,
                "Identifiants incorrects.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}