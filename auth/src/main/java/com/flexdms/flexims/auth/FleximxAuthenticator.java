package com.flexdms.flexims.auth;

import javax.persistence.EntityManager;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;

import com.flexdms.flexims.App;
import com.flexdms.flexims.users.FxUser;

public class FleximxAuthenticator extends AuthenticatingRealm {

	public static final String REALMNAME = "flexims";

	public FleximxAuthenticator() {
		super();
		this.setName(REALMNAME);
	}

	public FleximxAuthenticator(CacheManager cacheManager, CredentialsMatcher matcher) {
		super(cacheManager, matcher);
		this.setName(REALMNAME);
	}

	public FleximxAuthenticator(CacheManager cacheManager) {
		super(cacheManager);
		this.setName(REALMNAME);
	}

	public FleximxAuthenticator(CredentialsMatcher matcher) {
		super(matcher);
		this.setName(REALMNAME);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
		if (token instanceof UsernamePasswordToken) {
			UsernamePasswordToken token2 = (UsernamePasswordToken) token;
			EntityManager em = com.flexdms.flexims.App.getEM();
			FxUser user = FxUser.loadByEmail(em, token2.getUsername());
			if (user == null) {
				return null;
			}
			em.clear();
			em.getEntityManagerFactory().getCache().evict(user.getEntityImpl().getClass(), user.getId());
			em.getEntityManagerFactory().getCache()
					.evict(user.getUserSettings().getEntityImpl().getClass(), user.getUserSettings().getEntityImpl().getId());
			user = FxUser.loadByEmail(em, token2.getUsername());

			UserPrincipal principal = new UserPrincipal(user);
			SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(principal, user.getPassword(), ByteSource.Util.bytes(String
					.valueOf(user.getId())), user.getEmail());
			return authenticationInfo;

		}
		return null;
	}

}
