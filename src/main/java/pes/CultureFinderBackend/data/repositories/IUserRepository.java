package pes.CultureFinderBackend.data.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pes.CultureFinderBackend.data.models.User;

import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {

    /*
     * Descripció: Obté l'identificador intern d'un usuari loguejat mitjançant la seva apiToken
     */
    @Query(value = "SELECT s.user_id FROM sessions s WHERE s.token = :token", nativeQuery = true)
    String getUserIdByAPIToken(@Param("token") String apiToken);

    /*
     * Descripció: Comprova si existeix la sessió d'un usuari mitjançant el seu identificador intern
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Session s WHERE s.userId = :id")
    boolean existsSessionById(@Param("id") String userId);

    /*
     * Descripció: Comprova si existeix la sessió d'un usuari mitjançant la seva API token
     */
    @Query(value = "SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM sessions s WHERE s.token = :apiToken", nativeQuery = true)
    boolean existsSessionByToken(@Param("apiToken") String apiToken);

    /*
     * Descripció: Guarda una tupla amb la informació de la sessió iniciada d'un usuari
     * <userId>: Identificador intern de l'usuari
     * <apiToken>: API token de l'usuari que es vol loguejar
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO sessions (user_id, device_token, token) values(:id, :deviceToken, :token)", nativeQuery = true)
    void createSession(@Param("id") String userId, @Param("token") String apiToken, @Param("deviceToken") String deviceToken);

    /*
     * Descripció: Esborra la sessió d'un usuari que es troba loguejat al sistema
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM sessions WHERE token = :token", nativeQuery = true)
    void deleteSession(@Param("token") String apiToken);

    /*
    * Descripció: Obté la sessió d'un usuari donat el seu ID
    */
    @Query(value = "SELECT s.device_token FROM sessions s WHERE s.user_id = :userId", nativeQuery = true)
    String findDeviceTokenByUserId(String userId);

    /*
     * Descripció: Comprova si un usuari és administrador mitjançant el seu identificador
     */
    @Query(value = "SELECT u.is_admin FROM users u WHERE u.id = :id", nativeQuery = true)
    boolean isAdmin(@Param("id") String userId);

    /*
     * Descripció: Obté tots els identificadors dels usuaris loguejats al sistema
     */
    @Query(value = "SELECT s.user_id FROM sessions s", nativeQuery = true)
    List<String> findAllLoggedUserIds();
}
