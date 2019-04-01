package sn.cperf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import sn.cperf.dao.UserRepository;
import sn.cperf.model.User;

@Service
public class CperfServiceImpl implements CperfService {

	@Autowired
	UserRepository userRepository;

	@Override
	public User getLoged() {
		try {
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			return userRepository.findByUsername(username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}