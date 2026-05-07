import java.io.*;
import java.util.Scanner;

public class Authentification {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Login : ");
        String login = scanner.nextLine().trim();

        System.out.print("Mot de passe : ");
        String motDePasse = scanner.nextLine().trim();

        BufferedReader br = new BufferedReader(new FileReader("d:\\fichiers\\in.txt"));
        String ligne;
        boolean trouve = false;

        while ((ligne = br.readLine()) != null) {
            String[] parts = ligne.split(" ");
            if (parts.length == 2) {
                String fileLogin = parts[0];
                String filePass  = parts[1];
                if (fileLogin.equals(login) && filePass.equals(motDePasse)) {
                    trouve = true;
                    break;
                }
            }
        }

        br.close();
        scanner.close();

        if (trouve) {
            System.out.println("Authentification réussi");
        } else {
            System.out.println("PB Authentification");
        }
    }
}