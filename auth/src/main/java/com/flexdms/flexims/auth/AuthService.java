package com.flexdms.flexims.auth;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.flexdms.flexims.EntityDAO;
import com.flexdms.flexims.RunAsAdmin;
import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.rsutil.AppMsg;
import com.flexdms.flexims.rsutil.RSMsg;
import com.flexdms.flexims.users.FxUser;
import com.flexdms.flexims.util.SessionCtx;

/**
 * Service manage user authentication.
 * 
 * @author jason.zhang
 * 
 */
@Path("/auth")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RequestScoped
public class AuthService {

	@Context
	Request rs;

	@Inject
	EntityDAO dao;

	@Inject
	EntityManager em;

	@Inject
	SessionCtx sessionCtx;

	@Inject
	JaxbHelper jaxbHelper;

	@Path("/status")
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String status() {
		return "ok";
	}

	@Path("/auth")
	@POST
	@RunAsAdmin
	public AppMsg auth(LoginInfo loginInfo) {
		Subject currentUser = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(loginInfo.getEmail(), loginInfo.getPassword());
		// this is all you have to do to support 'remember me' (no config -
		// built in!):
		if (loginInfo.isRememberMe()) {
			token.setRememberMe(true);
		}
		AppMsg msg = new AppMsg();
		try {
			currentUser.login(token);
			msg.setMsg("Ok");
		} catch (Exception e) {
			msg.setStatuscode(1);
			msg.setMsg("Invalid Email or Password");
		}
		return msg;
	}

	@Path("/currentUser")
	@GET
	public FleximsDynamicEntityImpl currentUser() {
		FxUser user = (FxUser) sessionCtx.getAttr(FxUser.SESSION_USER_KEY);

		return user.getEntityImpl();
	}

	@Path("/accountSettings")
	@GET
	public FleximsDynamicEntityImpl currentUserSettings() {
		FxUser user = (FxUser) sessionCtx.getAttr(FxUser.SESSION_USER_KEY);

		return user.loadBy(em).getEntityImpl();
	}

	@Path("/logout")
	@GET
	public AppMsg logout(@Context HttpServletRequest request) {

		AppMsg msg = new AppMsg();

		Subject currentUser = SecurityUtils.getSubject();
		currentUser.logout();
		if (request.getSession(false) != null) {
			request.getSession(false).invalidate();
		}
		msg.setMsg("Ok");

		return msg;
	}

	@Path("/register")
	@POST
	@RunAsAdmin
	public AppMsg register(LoginInfo loginInfo) {
		AppMsg msg = new AppMsg();

		em.getTransaction().begin();
		FxUser fxUser = FxUser.createUser(em);
		fxUser.setName(loginInfo.getUsername());
		fxUser.setEmail(loginInfo.getEmail());
		fxUser.setPassword(loginInfo.getPassword());

		em.persist(fxUser.getEntityImpl());
		em.persist(fxUser.getUserSettings().getEntityImpl());
		em.flush();
		PasswordHasher.hashPasswordStatic(fxUser);
		em.getTransaction().commit();
		msg.setMsg("Ok");

		return msg;
	}

	@Path("/changepassword")
	@POST
	public AppMsg changePassword(LoginInfo loginInfo) {
		AppMsg msg = new AppMsg();
		FxUser user = (FxUser) sessionCtx.getAttr(FxUser.SESSION_USER_KEY);

		em.clear();

		em.getTransaction().begin();
		FleximsDynamicEntityImpl dEntityImpl = em.find(user.getEntityImpl().getClass(), user.getId());
		user = new FxUser(dEntityImpl);

		String hashedString = PasswordHasher.hashPassword(loginInfo.getOldPassword(), user.getId());
		if (!hashedString.equals(user.getPassword())) {
			msg.setStatuscode(1);
			msg.setMsg("current password is not correct");
			return msg;
		}
		user.setPassword(loginInfo.getPassword());
		PasswordHasher.hashPasswordStatic(user);
		em.getTransaction().commit();
		return msg;

	}

}
