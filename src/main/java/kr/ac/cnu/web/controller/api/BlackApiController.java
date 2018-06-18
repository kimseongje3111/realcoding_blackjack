package kr.ac.cnu.web.controller.api;

import kr.ac.cnu.web.exceptions.NoLoginException;
import kr.ac.cnu.web.exceptions.NoUserException;
import kr.ac.cnu.web.games.blackjack.GameRoom;
import kr.ac.cnu.web.games.blackjack.Player;
import kr.ac.cnu.web.model.User;
import kr.ac.cnu.web.repository.UserRepository;
import kr.ac.cnu.web.service.BlackjackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Created by rokim on 2018. 5. 21..
 */
@RestController
@RequestMapping("/api/black-jack")
@CrossOrigin
public class BlackApiController {
    @Autowired
    private BlackjackService blackjackService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public User login(@RequestBody String name) {
        return userRepository.findById(name).orElseThrow(() -> new NoUserException());
    }

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public User signup(@RequestBody String name) {
        // TODO check already used name
        Optional<User> userOptional = userRepository.findById(name);
        if (userOptional.isPresent()){
            throw new RuntimeException();
        }
        // TODO new user
        User user = new User(name, 50000);

        // TODO save in repository
        return userRepository.save(user);
    }

    @PostMapping("/rooms")
    public GameRoom createRoom(@RequestHeader("name") String name) {
        User user = this.getUserFromSession(name);

        return blackjackService.createGameRoom(user);
    }

    @PostMapping(value = "/rooms/{roomId}/bet", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GameRoom bet(@RequestHeader("name") String name, @PathVariable String roomId, @RequestBody long betMoney) {
        User user = this.getUserFromSession(name);

        return blackjackService.bet(roomId, user, betMoney);
    }

    @PostMapping(value = "/rooms/{roomId}/doubledown", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GameRoom doubledown(@RequestHeader("name") String name, @PathVariable String roomId, @RequestBody long betMoney) {
        User user = this.getUserFromSession(name);
        // 해당 이름의 플레이어 호출
        Player player = blackjackService.getGameRoom(roomId).getPlayerList().get(name);

        // 더블다운 후 보유금액 업데이트
        GameRoom gameRoom = blackjackService.doubledown(roomId,user,betMoney);
        // TODO set account
        user.setAccount(player.getBalance());
        // TODO save user
        userRepository.save(user);

        return gameRoom;
    }

    @PostMapping("/rooms/{roomId}/hit")
    public GameRoom hit(@RequestHeader("name") String name, @PathVariable String roomId) {
        User user = this.getUserFromSession(name);
        // 해당 이름의 플레이어 호출
        Player player = blackjackService.getGameRoom(roomId).getPlayerList().get(name);

        // 히트 후 보유금액 업데이트
        GameRoom gameRoom = blackjackService.hit(roomId,user);
        // TODO set account
        user.setAccount(player.getBalance());
        // TODO save user
        userRepository.save(user);

        return gameRoom;
    }

    @PostMapping("/rooms/{roomId}/stand")
    public GameRoom stand(@RequestHeader("name") String name, @PathVariable String roomId) {
        User user = this.getUserFromSession(name);
        // 해당 이름의 플레이어 호출
        Player player = blackjackService.getGameRoom(roomId).getPlayerList().get(name);

        // 스탠드 후 보유금액 업데이트
        GameRoom gameRoom = blackjackService.stand(roomId,user);
        // TODO set account
        user.setAccount(player.getBalance());
        // TODO save user
        userRepository.save(user);

        return gameRoom;
    }

    // 랭크 컨트롤러
    @PostMapping("/rooms/{roomid}/rank")
    public List rank(@RequestHeader("name") String name, @PathVariable String roomid){
        // 유저 레퍼스토리에 모든 원소들을 내림차순 정렬하여 리스트로 리턴
        List<User> users = userRepository.findAll(new Sort(Sort.Direction.DESC, "account"));
        return users;
    }

    @GetMapping("/rooms/{roomId}")
    public GameRoom getGameRoomData(@PathVariable String roomId) {
        return blackjackService.getGameRoom(roomId);
    }


    private User getUserFromSession(String name) {
        return userRepository.findById(name).orElseThrow(() -> new NoLoginException());
    }
}
