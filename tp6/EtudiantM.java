import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantM {

    public boolean create(Etudiant o) {
        String sql = "INSERT INTO etudiant (nom, prenom, sexe, filiere) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = Connexion.getCn().prepareStatement(sql);
            ps.setString(1, o.getNom());
            ps.setString(2, o.getPrenom());
            ps.setString(3, o.getSexe());
            ps.setString(4, o.getFiliere());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
            return false;
        }
    }

    public boolean delete(Etudiant o) {
        String sql = "DELETE FROM etudiant WHERE id = ?";
        try {
            PreparedStatement ps = Connexion.getCn().prepareStatement(sql);
            ps.setInt(1, o.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
            return false;
        }
    }

    public boolean update(Etudiant o) {
        String sql = "UPDATE etudiant SET nom = ?, prenom = ?, sexe = ?, filiere = ? WHERE id = ?";
        try {
            PreparedStatement ps = Connexion.getCn().prepareStatement(sql);
            ps.setString(1, o.getNom());
            ps.setString(2, o.getPrenom());
            ps.setString(3, o.getSexe());
            ps.setString(4, o.getFiliere());
            ps.setInt(5, o.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
            return false;
        }
    }

    public Etudiant findById(int id) {
        String sql = "SELECT * FROM etudiant WHERE id = ?";
        try {
            PreparedStatement ps = Connexion.getCn().prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Etudiant(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("sexe"),
                        rs.getString("filiere")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return null;
    }

    public List<Etudiant> findAll() {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiant";
        try {
            Statement st = Connexion.getCn().createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                etudiants.add(new Etudiant(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("sexe"),
                        rs.getString("filiere")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
        }
        return etudiants;
    }
}
