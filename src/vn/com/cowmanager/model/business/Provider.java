package vn.com.cowmanager.model.business;

import java.util.ArrayList;
import java.util.List;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.DAttr.Type;
import vn.com.cowmanager.model.arrangement.Person;
import vn.com.cowmanager.model.arrangement.Cow;

@DClass(schema = "bussiness")
public class Provider extends Person {

	private static int idCounter = 0;

	@DAttr(name = "manufacturer", type = Type.String, length = 25)
	private String manufacturer;

	@DAttr(name = "lstCow", type = Type.Collection, filter = @Select(clazz = Cow.class), optional = false, serialisable = false)
	@DAssoc(ascName = "provider-has-cow", ascType = AssocType.One2Many, endType = AssocEndType.One, role = "provider", associate = @Associate(cardMax = 25, cardMin = 1, type = Cow.class))
	private List<Cow> lstCow;
	private static int cowCount;

	public Provider(String id, String name, String address, String phoneNum, String manufacturer) {
		super(id, name, address, phoneNum);
		this.manufacturer = manufacturer;

		lstCow = new ArrayList<>();
		cowCount = 0;
	}

	public Provider(String name, String address, String phoneNum, String manufacturer) {
		this(null, name, address, phoneNum, manufacturer);
	}

	public Provider(String name, String address, String phoneNum) {
		this(null, name, address, phoneNum);
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association:
	// @MemberRef(name="students")
	public boolean addCow(Cow s) {
		if (!this.lstCow.contains(s)) {
			lstCow.add(s);
		}

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewCow(Cow s) {
		lstCow.add(s);
		cowCount++;

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addCow(List<Cow> lstCow) {
		for (Cow s : lstCow) {
			if (!this.lstCow.contains(s)) {
				this.lstCow.add(s);
			}
		}

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewCow(List<Cow> lstCow) {
		this.lstCow.addAll(lstCow);
		cowCount += lstCow.size();

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association:
	// @MemberRef(name="students")
	public boolean removeCow(Cow s) {
		boolean removed = lstCow.remove(s);

		if (removed) {
			cowCount--;
		}

		// no other attributes changed
		return false;
	}

	public List<Cow> getLstCow() {
		return lstCow;
	}

	public void setLstCow(List<Cow> lstCow) {
		this.lstCow = lstCow;
		cowCount = lstCow.size();
	}

	public static int getCowCount() {
		return cowCount;
	}

	public static void setCowCount(int cowCount) {
		Provider.cowCount = cowCount;
	}

	// automatically generate the next student id
	protected String nextId(String id) throws ConstraintViolationException {
		if (id == null) { // generate a new id
			idCounter++;
			return "PR" + idCounter;
		} else {
			// update id
			int num;
			try {
				num = Integer.parseInt(id.substring(2));
			} catch (RuntimeException e) {
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
						new Object[] { id });
			}

			if (num > idCounter) {
				idCounter = num;
			}

			return id;
		}
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

}
