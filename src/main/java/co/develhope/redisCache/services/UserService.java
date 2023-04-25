package co.develhope.redisCache.services;

import co.develhope.redisCache.entities.User;
import co.develhope.redisCache.entities.jpa.UserJPA;
import co.develhope.redisCache.entities.redis.UserRedis;
import co.develhope.redisCache.repositories.jpa.UserRepositoryJPA;
import co.develhope.redisCache.repositories.redis.UserRepositoryRedis;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepositoryJPA userRepositoryJPA;

    @Autowired
    private UserRepositoryRedis userRepositoryRedis;

    //ho cambiato il nome della variabile UserJPA da user a userJPA per chiarezza
    public static UserRedis convertData(UserJPA userJPA){
        UserRedis userRedis = new UserRedis();
        BeanUtils.copyProperties(userJPA, userRedis);
        return userRedis;
    }

    //ho cambiato il nome della variabile UserRedis da user a userRedis per chiarezza
    //e ho cambiato il nome della variabile UserJPA da userRedis (refuso???) a userJPA
    //Lorenzo fai meso casinooooo, please!!!
    public UserJPA convertData(UserRedis userRedis){
        UserJPA userJPA = new UserJPA();
        BeanUtils.copyProperties(userRedis, userJPA);
        return userJPA;
    }

    //perché deve settare l'id nullo???
    //create: quando creo un'entità NON C'é la CACHE!!!
    public UserJPA create(UserJPA user) {
        if(user == null) return null;
        //"in modo tale che non faccia un "edit" quando fa il create" (???)
        //user.setId(null);
        return userRepositoryJPA.save(user);
    }

    //anche in questo caso non c'è la REDIS cahce
    //perché non posso mettermi in cache tutti gli utenti
    //(starei facendo una copia del database!)
    public List<UserJPA> readAll() {
        return userRepositoryJPA.findAll();
    }

    public UserJPA readOne(Long id) {
        //PER PRIMA COSA VOGLIO VEDERE SE C'è QUALCOSA NELLA REDIS!
        Optional<UserRedis> userRedis = userRepositoryRedis.findById(id);
        if(userRedis.isPresent()){
            return convertData(userRedis.get());
        }else {
            UserJPA userFromDB = userRepositoryJPA.getById(id);
            //una volta che me lo sono preso dal DB
            //voglio salvarlo anche nella cache!
            //e per farlo devo fare una conversione
            //da UserJPA a UserRedis
            if (userFromDB == null ) return null;
            userRepositoryRedis.save(convertData(userFromDB));
            return userFromDB;
        }
    }

    //quando faccio l'update
    //devo ricordami anche della redis cache!
    //N.B. USO di setId(id) !!!!!!!!!
    public UserJPA update(Long id, UserJPA user) {
        if(user == null) return null;
        //ma fa così per non dover riprendere e cancellare prima l'utente con quell'id, giusto?
        //cioè in questo modo la nuova versione del mio userJPA sovrascrive
        //la versione precedente?
        user.setId(id);
        UserJPA newUser = userRepositoryJPA.save(user);

        Optional<UserRedis> userRedis = userRepositoryRedis.findById(id);
        //se lo userRedis è presente io posso fare due cose:
        //- o lo cancello: userRepositoryRedis.deleteById(id)
        //- oppure lo salvo!
        if(userRedis.isPresent()) {
            //userRepositoryRedis.deleteById(id); // FASTER update - SLOWER read
            userRepositoryRedis.save(convertData(newUser)); // SLOWER update - FASTER read
        }

        return user;
    }

    //stessa cosa qui, devo cancellare lo user
    //anche dalla redisCache!
    public void delete(Long id) {
        userRepositoryJPA.deleteById(id);
        userRepositoryRedis.deleteById(id);
    }

    public void readOneFast(Long id) {
    }
}
