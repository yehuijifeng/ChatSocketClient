package data;

import java.util.ArrayList;
import java.util.List;

import bean.UserGroupBean;
import bean.UserInfoBean;

public class UserData {
	/**
	 * 模拟数据库的用户信息，这里创建id不同的用户信息
	 * 
	 * @param userId
	 * @return
	 */
	public static UserInfoBean getUserInfoBean(int userId) {
		UserInfoBean userInfoBean = new UserInfoBean();
		userInfoBean.setUserIcon("用户头像");
		userInfoBean.setUserId(userId);
		userInfoBean.setUserName("admin");
		userInfoBean.setUserPwd("123123132a");
		userInfoBean.setGroupList(getUserGroup());
		return userInfoBean;
	}

	private static List<UserGroupBean> getUserGroup() {
		List<UserGroupBean> list = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			UserGroupBean userGroupBean = new UserGroupBean();
			userGroupBean.setGroupId(i);
			userGroupBean.setGroupNumberByMax(500);
			userGroupBean.setGroupNumberByCurrent(20);
			userGroupBean.setGroupType(i);
			userGroupBean.setGroupUserType(-1);
			list.add(userGroupBean);
		}
		return list;
	}
}
