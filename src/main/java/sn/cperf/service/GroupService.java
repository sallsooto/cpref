package sn.cperf.service;

import java.util.Date;
import java.util.List;

import sn.cperf.util.GroupProcesses;

public interface GroupService {
   List<GroupProcesses> getGroupProcesses(Date date);
}
