package com.flexdms.flexims.users;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.dynamic.DynamicEntity;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

public class FxUser extends FxRole {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String SESSION_USER_KEY = "fxuser";
	public static final String TYPE_NAME = "FxUser";

	public FxUser() {
		super();
	}

	public FxUser(FleximsDynamicEntityImpl entityImpl) {
		super(entityImpl);
	}

	public String getEmail() {
		return getUserSettings().getEmail();
	}

	public String getPassword() {
		return getUserSettings().getPassword();
	}

	public FxUser setEmail(String value) {
		getUserSettings().setEmail(value);
		return this;
	}

	public FxUser setPassword(String value) {
		getUserSettings().setPassword(value);
		return this;
	}

	FxUserSettings settings = null;

	public FxUserSettings getUserSettings() {
		if (settings == null) {
			settings = loadBy(App.getEM());

		}
		return settings;
	}

	public static FxUser loadByEmail(EntityManager em, String email) {
		TypedQuery<? extends DynamicEntity> jplquery = em.createQuery("select s from FxUserSettings s where s.Email= :email",
				JpaHelper.getEntityClass(em, "FxUserSettings"));
		jplquery.setParameter("email", email);
		List<? extends DynamicEntity> list = jplquery.getResultList();
		if (list == null || list.isEmpty()) {
			return null;
		}
		FxUserSettings settings = new FxUserSettings((FleximsDynamicEntityImpl) list.get(0));
		FxUser user = new FxUser((FleximsDynamicEntityImpl) settings.getEntityImpl().get(FxUserSettings.PROP_NAME_PROP_USER));
		user.settings = settings;
		return user;

	}
	public static FxUser loadById(EntityManager em, long id) {
		return new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, FxUser.TYPE_NAME), id));
	}

	public FxUserSettings loadBy(EntityManager em) {
		TypedQuery<? extends DynamicEntity> jplquery = em.createQuery("select s from FxUserSettings s where s.FxUser= :fxUser",
				JpaHelper.getEntityClass(em, "FxUserSettings"));
		jplquery.setParameter("fxUser", this.getEntityImpl());
		return new FxUserSettings((FleximsDynamicEntityImpl) jplquery.getSingleResult());

	}

	public static FxUser createUser(EntityManager em) {

		FleximsDynamicEntityImpl user = JpaHelper.createNewEntity(em, FxUser.TYPE_NAME);
		FleximsDynamicEntityImpl settings = JpaHelper.createNewEntity(em, FxUserSettings.TYPE_NAME);
		settings.set(FxUserSettings.PROP_NAME_PROP_USER, user);
		FxUser fxUser = new FxUser(user);
		FxUserSettings fxUserSettings = new FxUserSettings(settings);
		fxUser.settings = fxUserSettings;
		return fxUser;
	}
}
