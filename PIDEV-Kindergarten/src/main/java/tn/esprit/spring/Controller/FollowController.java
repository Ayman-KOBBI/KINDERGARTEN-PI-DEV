package tn.esprit.spring.Controller;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import tn.esprit.spring.Repository.FollowRepository;
import tn.esprit.spring.Repository.FriendRepository;
import tn.esprit.spring.Repository.UserRepository;
import tn.esprit.spring.Services.FollowService;
import tn.esprit.spring.Services.FriendService;
import tn.esprit.spring.entity.Follow;
import tn.esprit.spring.entity.Friend;
import tn.esprit.spring.entity.Oppor;
import tn.esprit.spring.entity.User;

@RestController
@RequestMapping("/Follow")
@EnableScheduling
public class FollowController {
	@Autowired
	FollowService followservice;
	
	@Autowired
	FollowRepository followRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	FriendRepository friendRepository;
	
	@Autowired
	FriendService friendService;
	
	
	
	@RequestMapping(method=RequestMethod.POST,value="/{username}")
	public String sendDemand(@PathVariable String username)
	{
		User reciever = new User();
		reciever=userRepository.findByUsername(username).get();
		User sender = new User();
		sender=userRepository.findByUsername(HomeController.connectedUser).get();
		
		List<Follow> follows=new ArrayList<>();
		follows=followRepository.findFollowByUserReciver(reciever);
		for (Follow f : follows) {
			if( f.getId_Sender()==sender.getId()){
				return "Demande is already sended";
				
			}
			
		}
		
		System.out.println(HomeController.connectedUser);
		followservice.SendDemande(username, HomeController.connectedUser);
		 return "Demande sended with access";
		
	}
	@RequestMapping(value="/sended")
	public List<Follow> mySendedDemandes()
	{
		return followservice.MySendedDemandes(HomeController.connectedUser);
	}
	@RequestMapping(value="/reciever")
	public List<String> RecivedDemandes()
	{
		return followservice.userssendedtome(HomeController.connectedUser);
	}
	@RequestMapping(method=RequestMethod.PUT,value="/f/{id}")
	public String AcceptdemandeStatus(@PathVariable Long  id)
	{
		
		Follow follow=new Follow();
		Follow follow1=new Follow();
		follow1=followRepository.cherchefollow(id, HomeController.connectedUser).stream().findFirst().get();
		System.out.println(follow1.getId());
		follow=followRepository.findById(follow1.getId()).get();
		
		follow.setEtat("Accepted");
		followservice.changeStatus(follow, follow1.getId());
		int idd=follow1.getId();
		follow = followservice.allFollows().stream().filter(d->d.getId()==idd).findFirst().get();
		
		boolean s=false;
		if(follow.getEtat().equals("Accepted"))	{
			
		for(Friend f :friendService.allFriend()){
			if((f.getUser1()==follow.getId_Sender()&& f.getUser2()==follow.getUserReciver().getId())||(f.getUser2()==follow.getId_Sender()&& f.getUser1()==follow.getUserReciver().getId()))
				s=true;
			
		}
		if(s==true)
			return "cc";
		else {
			if(follow.getUserReciver().getUsername().equals(HomeController.connectedUser))
			friendService.addFriend(new Friend(follow.getId_Sender(),follow.getUserReciver().getId(),false,false,false,null));
			else
				return "bb";
		}
		}
		else
			followservice.DeleteFollow(follow);	
		return "dd";
	}
	@RequestMapping(method=RequestMethod.PUT,value="/{etat}&{id}")
	public String demandeStatus(@PathVariable String  etat,@PathVariable int  id)
	{
		
		Follow follow=new Follow();
		follow=followRepository.findById(id).get();
		follow.setEtat(etat);
		followservice.changeStatus(follow, id);
		follow = followservice.allFollows().stream().filter(d->d.getId()==id).findFirst().get();
		boolean s=false;
		if(follow.getEtat().equals("Accepted"))	{
			
		for(Friend f :friendService.allFriend()){
			if((f.getUser1()==follow.getId_Sender()&& f.getUser2()==follow.getUserReciver().getId())||(f.getUser2()==follow.getId_Sender()&& f.getUser1()==follow.getUserReciver().getId()))
				s=true;
			
		}
		if(s==true)
			return "your are already friends";
		else {
			if(follow.getUserReciver().getUsername().equals(HomeController.connectedUser))
			friendService.addFriend(new Friend(follow.getId_Sender(),follow.getUserReciver().getId(),false,false,false,null));
			else
				return"forbidden to accpet this follow";
		}
		}
		else
			followservice.DeleteFollow(follow);	
		return "thinks for acception my demande to be friend";
	}
	@RequestMapping(value="/friends")
	public List<User> myfriend()
	{
		
		return friendService.myFriends(HomeController.connectedUser);
	}
	@RequestMapping(value="/friendsnon")
	public List<User> friendNompreférer(){
		return friendService.friendNonPreferer(HomeController.connectedUser);
		
	}

	@RequestMapping(method=RequestMethod.PUT,value="/block/{id}")
	public void blockFriend(@PathVariable Long id){
		friendService.BlockFriend(id, HomeController.connectedUser);
		
		
	}
	
	@RequestMapping(method=RequestMethod.PUT,value="/blocksign/{id}")
	public String blockSignFriend(@PathVariable Long id){
		return friendService.BlocksigFriend(id, HomeController.connectedUser);
		
		
	}
	
//	
//	
}