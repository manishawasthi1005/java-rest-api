package com.manish.javarestapi.service;

import com.manish.javarestapi.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.ErrorResponseException;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class UserServiceImpl {
    private static List<User> usersList = new ArrayList<User>();
    static{
        usersList.add(new User(UUID. randomUUID(), "Adam", LocalDateTime.now().minusYears(30)));
        usersList.add(new User(UUID. randomUUID(), "John", LocalDateTime.now().minusYears(25)));
        usersList.add(new User(UUID. randomUUID(), "Sonia", LocalDateTime.now().minusYears(20)));
    }

    public List<User> getAllUsers(){
        return usersList;
    }

    public User findUserById(UUID id){
        if(usersList.stream().anyMatch(user -> user.getId().equals(id)))
            return usersList.stream().filter(user -> user.getId().equals(id)).findFirst().get();
        else
            return null;
    }
    public Integer findUserIndex(UUID id){
        //Stream Way
        /*IntStream.range(0, usersList.size())
                .filter(i -> usersList.get(i).getId().equals(id))
                .findFirst()
                .orElse(-1);*/
        for(Integer j =0; j< usersList.size(); j++){
            if(usersList.get(j).getId().equals(id))
                return j;
        }
        return -1;
    }
    public void saveOrUpdateUser(User user){
        if(findUserById(user.getId()) != null){
            //update the user
            User userToBeUpdated =  findUserById(user.getId());
            Integer userIndex = findUserIndex(userToBeUpdated.getId());
            if(userIndex != -1){
                usersList.set(userIndex, user);
            }else{
                throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
            }
        }
        else{
            usersList.add(user);
        }
    }
    public ResponseEntity<User> createUser(User user) {
        UUID uuid = UUID.randomUUID();
        user.setId(uuid);
        saveOrUpdateUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    public ResponseEntity<User> updateFullUser(UUID id, User user) {
        //check if id is not out of index
        try {
            User userObj = findUserById(id);
            if (Objects.isNull(userObj)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                user.setId(id);
                saveOrUpdateUser(user);
                return new ResponseEntity<>(usersList.get(findUserIndex(id)), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<User> updatePartialUser(UUID id, Map<Object, Object> patchFields) {
        //check if id is available or not
        try {
            User user = findUserById(id);
            patchFields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(User.class, (String) key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, user, value);
            });
            saveOrUpdateUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
