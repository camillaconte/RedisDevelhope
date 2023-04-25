package co.develhope.redisCache;

import co.develhope.redisCache.entities.User;
import co.develhope.redisCache.entities.jpa.UserJPA;
import co.develhope.redisCache.entities.redis.UserRedis;
import co.develhope.redisCache.repositories.redis.UserRepositoryRedis;
import co.develhope.redisCache.services.UserService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisCacheMock.class)
public class RedisCacheMockTest {

    @Autowired
    private UserRepositoryRedis userRepositoryRedis;

    @Autowired
    private UserService userService;

    //perché setta anche l'id?
    //forse perché il Mock redis cache non è in grado di assegnare id?
    //però dovrebbe!
    @Test
    public void shouldWriteOnRedisCache(){
        UserRedis userRedis = new UserRedis();
        userRedis.setDomicileCity("Milano");
        userRedis.setId(1L);
        userRedis.setEmail("email@email.com");
        userRedis.setFirstName("MyName");

        UserRedis userSavedInRedisCache = userRepositoryRedis.save(userRedis);

        Assertions.assertNotNull(userSavedInRedisCache);
    }

    //MA NON ABBIAMO DETTO CHE IL CREATE NON PREVEDE
    //UN SALVATAGGIO NELLA Cache?
    //facciamo finta di sì?
    //potrebbe darci dei problemi quel "setId(null)" !!!
    // Quindi nel metodo del service l'ho commentato
    //però comunque non va bene perché incasino il vero database
    //dovrei lavorare con il Mock DB (H2)
    @Test
    public UserRedis shouldCreateUser(){
        UserJPA userJPA = new UserJPA(1L, "Mario", "Rossi", "sdfegtds", "mario.rossi@yahoo.it", "ghfgte536",
                "Via Roma", "Milano", "5", "Italia", "RSSMRA34T19R345P");
        userService.create(userJPA);
        //però qui io non testo con il Mock DB (H2) il fatto che venga salvato lo userJPA
        //ci vorrebbe un'altra classe per questi test!!!
        UserRedis userRedis = UserService.convertData(userJPA);
        UserRedis userSavedInRedisCache = userRepositoryRedis.save(userRedis);
        Assertions.assertNotNull(userSavedInRedisCache);
        return userSavedInRedisCache;
    }

    @Test
    public void shouldGetUserFromCache(){
        //per rendere il test indipendente
        //devo comunque creare prima uno User
        //con un metodo che restituisca uno UserRedis
        UserRedis userSavedInCache = shouldCreateUser();
        //ma quando faccio la get
        // come faccio a sapere che mi stanno dando lo User della cache
        //invece che lo User del DB?
        //uso l'id dello userSavedInCache
        UserJPA userJPA = userService.readOne(userSavedInCache.getId());
        //se lo userJPA fosse nullo, vorrebbe dire che non è stato trovato
        //il corrispondente user nella redis cache
        Assertions.assertNotNull(userJPA);
    }

    @Test
    public void shouldDeleteUser(){
        //come sopra: per rendere il test indipendente
        //devo comunque creare prima uno User
        //con un metodo che restituisca uno UserRedis
        UserRedis userSavedInCache = shouldCreateUser();
        userService.delete(userSavedInCache.getId());
        //controllo che anche lo User salvato nella cache
        //non esista più
        Assertions.assertNull(userSavedInCache);
    }

    @Test
    public void shouldUpdateUser(){
        //userService.update();
    }


}
