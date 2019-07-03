package sn.cperf.service;

import sn.cperf.model.User;

public interface UserService {
	public boolean deleteUser(Long id);
	public boolean deleteUser(User user);
}
