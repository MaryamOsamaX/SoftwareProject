package fci.software.project.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fci.software.project.classes.LoginData;


@Controller
public class UserController {
	@Autowired
	private UserRepo userRepo;

	@RequestMapping(method = RequestMethod.GET, value = "/login")
	public String loginView(Model model) {
		LoginData loginData = new LoginData();
		model.addAttribute("logindata", loginData);
		return "index";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/login")
	public String validateLogin(Model model, @ModelAttribute("logindata") LoginData logindata)

	{
		Optional<User> x;
		x = userRepo.findById(logindata.getuId());
		if (x.isPresent()) {
			if (!x.get().getPassword().equals(logindata.getPassword())) {
				logindata.setResult("Wrong password");
				model.addAttribute("logindata", logindata);
				return "index";
			} else
				return "redirect:/home";
		} else {
			logindata.setResult("This user not exist");
			model.addAttribute("logindata", logindata);

		}

		return "index";

	}
	@RequestMapping(method = RequestMethod.GET, value = "/signup")
	public String signupView(Model model)

	{
		User userData = new User();
		model.addAttribute("userdata", userData);

		return "signup";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/signup")
	public void validateSignUp(Model model, @ModelAttribute("userdata") User userData)

	{
		Optional<User> test;

		test = userRepo.findById(userData.getUserId());
		if (test.isPresent()) {

			model.addAttribute("exception", "This user id is already exist ");

		} else {
              userRepo.save(userData);
              model.addAttribute("signupresult", "Added Successfully!");
              
		}
	}
	@RequestMapping(method = RequestMethod.GET, value = "/home")
	public String homeView()

	{
		
		return "home";
	}

	


}
