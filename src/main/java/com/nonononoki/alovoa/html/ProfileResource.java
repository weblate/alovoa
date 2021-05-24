package com.nonononoki.alovoa.html;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.nonononoki.alovoa.Tools;
import com.nonononoki.alovoa.component.TextEncryptorConverter;
import com.nonononoki.alovoa.entity.User;
import com.nonononoki.alovoa.model.ProfileWarningDto;
import com.nonononoki.alovoa.model.UserDto;
import com.nonononoki.alovoa.repo.GenderRepository;
import com.nonononoki.alovoa.repo.UserIntentionRepository;
import com.nonononoki.alovoa.service.AuthService;

@Controller
public class ProfileResource {

	@Autowired
	private AuthService authService;

	@Autowired
	private GenderRepository genderRepo;

	@Autowired
	private UserIntentionRepository userIntentionRepo;

	@Autowired
	private AdminResource adminResource;

	@Autowired
	private TextEncryptorConverter textEncryptor;

	@Value("${app.profile.image.max}")
	private int imageMax;

	@Value("${app.vapid.public}")
	private String vapidPublicKey;

	@Value("${app.age.legal}")
	private int ageLegal;

	@Value("${app.media.max-size}")
	private int mediaMaxSize;

	@Value("${app.intention.delay}")
	private long intentionDelay;

	public static final String url = "/profile";

	public static String getUrl() {
		return url;
	}

	@GetMapping(url)
	public ModelAndView profile() throws Exception {

		User user = authService.getCurrentUser();
		if (user.isAdmin()) {
			return adminResource.admin();
		} else {
			int age = Tools.calcUserAge(user);
			boolean isLegal = age >= ageLegal;
			ModelAndView mav = new ModelAndView("profile");
			mav.addObject("user", UserDto.userToUserDto(user, user, textEncryptor, UserDto.ALL));
			mav.addObject("genders", genderRepo.findAll());
			mav.addObject("intentions", userIntentionRepo.findAll());
			mav.addObject("imageMax", imageMax);
			mav.addObject("vapidPublicKey", vapidPublicKey);
			mav.addObject("isLegal", isLegal);
			mav.addObject("mediaMaxSize", mediaMaxSize);

			boolean showIntention = false;
			Date now = new Date();
			if (user.getDates().getIntentionChangeDate() == null
					|| now.getTime() >= user.getDates().getIntentionChangeDate().getTime() + intentionDelay) {
				showIntention = true;
			}
			mav.addObject("showIntention", showIntention);

			ProfileWarningDto warning = getWarnings(user);

			mav.addObject("hasWarning", warning.isHasWarning());
			mav.addObject("noProfilePicture", warning.isNoProfilePicture());
			mav.addObject("noDescription", warning.isNoDescription());
			mav.addObject("noIntention", warning.isNoIntention());
			mav.addObject("noGender", warning.isNoGender());
			mav.addObject("noLocation", warning.isNoLocation());

			return mav;
		}
	}

	@GetMapping("/profile/warning")
	public String warning(Model model) throws Exception {

		User user = authService.getCurrentUser();
		ProfileWarningDto warning = getWarnings(user);

		model.addAttribute("hasWarning", warning.isHasWarning());
		model.addAttribute("noProfilePicture", warning.isNoProfilePicture());
		model.addAttribute("noDescription", warning.isNoDescription());
		model.addAttribute("noIntention", warning.isNoIntention());
		model.addAttribute("noGender", warning.isNoGender());
		model.addAttribute("noLocation", warning.isNoLocation());

		return "fragments::profile-warning";
	}

	private ProfileWarningDto getWarnings(User user) {

		boolean hasWarning = false;
		boolean noProfilePicture = false;
		boolean noDescription = false;
		boolean noIntention = false;
		boolean noGender = false;
		boolean noLocation = false;

		if (user.getProfilePicture() == null) {
			noProfilePicture = true;
			hasWarning = true;
		}
		if (user.getDescription() == null) {
			noDescription = true;
			hasWarning = true;
		} else if (user.getDescription().isEmpty()) {
			noDescription = true;
			hasWarning = true;
		}
		if (user.getIntention() == null) {
			noIntention = true;
			hasWarning = true;
		}
		if (user.getPreferedGenders() == null) {
			noGender = true;
			hasWarning = true;
		} else if (user.getPreferedGenders().size() == 0) {
			noGender = true;
			hasWarning = true;
		}
		if (user.getLocationLatitude() == null) {
			noLocation = true;
			hasWarning = true;
		}

		return ProfileWarningDto.builder().hasWarning(hasWarning).noDescription(noDescription).noGender(noGender)
				.noIntention(noIntention).noLocation(noLocation).noProfilePicture(noProfilePicture).build();
	}
}
