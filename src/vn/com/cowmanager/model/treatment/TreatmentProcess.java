package vn.com.cowmanager.model.treatment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.util.Tuple;
import vn.com.cowmanager.model.arrangement.Cage;
import vn.com.cowmanager.model.feeding.Storage;

@DClass(schema = "treatment")
public class TreatmentProcess {

	private static int idCounter = 0;

	@DAttr(name = "id", type = Type.String, id = true, auto = true, optional = false, mutable = false, length = 10)
	private String id;

	@DAttr(name = "cage", type = Type.Domain, optional = false, length = 6)
	@DAssoc(ascName = "treatmentprocess-has-drug", ascType = AssocType.One2Many, endType = AssocEndType.Many, role = "TreatmentProcess", associate = @Associate(cardMax = 25, cardMin = 1, type = Cage.class))
	private Cage cage;

	@DAttr(name = "storage", type = Type.Domain, length = 6)
	@DAssoc(ascName = "treatmentprocess-has-storage", ascType = AssocType.One2Many, endType = AssocEndType.Many, role = "TreatmentProcess", associate = @Associate(cardMax = 25, cardMin = 1, type = Storage.class))
	private Storage storage;

	@DAttr(name = "totalAmountInStorage", type = Type.Double, mutable = false, length = 6)
	private Double totalAmountInStorage;

	@DAttr(name = "amountForVetting", type = Type.Double, optional = false, length = 6)
	private Double amountForVetting;

	@DAttr(name = "doctor", type = Type.Domain, length = 6)
	@DAssoc(ascName = "treatmentprocess-has-doctor", ascType = AssocType.One2Many, endType = AssocEndType.Many, role = "TreatmentProcess", associate = @Associate(cardMax = 25, cardMin = 1, type = Doctor.class))
	private Doctor doctor;

	@DAttr(name = "dateBegin", type = Type.String, length = 15)
	private String dateBegin;

	@DAttr(name = "duration", type = Type.Integer, length = 6)
	private Integer duration;

	@DAttr(name = "dateLeftForTreatment", type = Type.Integer, mutable = false, length = 6)
	private Integer dateLeftForTreatment;

	public TreatmentProcess(String id, Cage cage, Storage storage, Double totalAmountInStorage, Double amountForVetting,
			Doctor doctor, String dateBegin, Integer duration, Integer dateLeftForTreatment) {
		this.id = nextId(id);
		this.cage = cage;
		this.storage = storage;
		this.amountForVetting = amountForVetting;
		this.doctor = doctor;
		if (validateDate(dateBegin)) {
			this.dateBegin = dateBegin;
		} else {
			throw new NotPossibleException(null,
					"Ä�á»‹nh dáº¡ng ngÃ y khÃ´ng há»£p lá»‡, ngÃ y Ä‘Æ°á»£c Ä‘á»‹nh dáº¡ng Ä‘Ãºng theo: dd/MM/yyyy");
		}
		this.duration = duration;
		calAutoGeneratedValues();
		Double doubleNum = storage.getQuantity() - amountForVetting;
		storage.setQuantity(doubleNum.intValue());
	}

	public TreatmentProcess(@AttrRef("cage") Cage cage, @AttrRef("storage") Storage storage,
			@AttrRef("totalAmountInStorage") Double totalAmountInStorage,
			@AttrRef("amountForVetting") Double amountForVetting, @AttrRef("doctor") Doctor doctor,
			@AttrRef("dateBegin") String dateBegin, @AttrRef("duration") Integer duration,
			@AttrRef("dateLeftForTreatment") Integer dateLeftForTreatment) {
		this(null, cage, storage, totalAmountInStorage, amountForVetting, doctor, dateBegin, duration,
				dateLeftForTreatment);
	}

	public TreatmentProcess(@AttrRef("cage") Cage cage, @AttrRef("storage") Storage storage,
			@AttrRef("totalAmountInStorage") Double totalAmountInStorage,
			@AttrRef("amountForVetting") Double amountForVetting, @AttrRef("dateBegin") String dateBegin,
			@AttrRef("duration") Integer duration, @AttrRef("dateLeftForTreatment") Integer dateLeftForTreatment) {
		this(null, cage, storage, totalAmountInStorage, amountForVetting, null, dateBegin, duration,
				dateLeftForTreatment);
	}

	// automatically generate the next student id
	private String nextId(String id) throws ConstraintViolationException {
		if (id == null) { // generate a new id
			idCounter++;
			return "TP" + idCounter;
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

	/**
	 * @requires minVal != null /\ maxVal != null
	 * @effects update the auto-generated value of attribute <tt>attrib</tt>,
	 *          specified for <tt>derivingValue</tt>, using
	 *          <tt>minVal, maxVal</tt>
	 */
	@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
	public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
			throws ConstraintViolationException {

		if (minVal != null && maxVal != null) {
			// TODO: update this for the correct attribute if there are more
			// than one auto attributes of this class

			String maxId = (String) maxVal;

			try {
				int maxIdNum = Integer.parseInt(maxId.substring(2));

				if (maxIdNum > idCounter) // extra check
					idCounter = maxIdNum;

			} catch (RuntimeException e) {
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
						new Object[] { maxId });
			}
		}
	}

	public void calAutoGeneratedValues() {
		getAmountFromStorage();
		calDayNumber();
	}

	private void getAmountFromStorage() {
		totalAmountInStorage = storage.getQuantity().doubleValue();
	}

	private void calDayNumber() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date dDateBegin = null;
		try {
			dDateBegin = df.parse(dateBegin);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date curDate = new Date();

		int dayNo = (int) ((curDate.getTime() - dDateBegin.getTime()) / (1000 * 60 * 60 * 24));
		if (dayNo >= 0) {
			dateLeftForTreatment = (int) duration - dayNo;
		} else {
			dateLeftForTreatment = 0;
		}

	}

	private boolean validateDate(String date) throws NotPossibleException {
		// 20/01/1997
		String strNum1 = date.substring(0, 2);
		int num1 = Integer.parseInt(strNum1);
		String strNum2 = date.substring(3, 5);
		int num2 = Integer.parseInt(strNum2);
		String strNum3 = date.substring(6, 10);
		int num3 = Integer.parseInt(strNum3);
		String slash1 = date.substring(2, 3);
		String slash2 = date.substring(5, 6);
		String slash = "/";
		if (slash1.equals(slash) && slash2.equals(slash) && num1 <= 31 && num2 <= 12 && num3 >= 1961) {
			return true;
		}
		return false;
	}

	public String toString() {
		return "TreatmentProcess(" + id + "," + amountForVetting + "," + cage.getName() + ")";
	}

	public String getId() {
		return id;
	}

	public Cage getCage() {
		return cage;
	}

	public void setCage(Cage cage) {
		if (cage != null && !cage.equals(this.cage)) {
			this.cage = cage;
			calAutoGeneratedValues();
		}
	}

	public Double getAmountForVetting() {
		return amountForVetting;
	}

	public void setAmountForVetting(Double amount) {
		this.amountForVetting = amount;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public String getDateBegin() {
		return dateBegin;
	}

	public void setDateBegin(String dateBegin) {
		if (dateBegin != null && !dateBegin.equals(this.dateBegin)) {
			this.dateBegin = dateBegin;
			calAutoGeneratedValues();
		}
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		if (duration != null && !duration.equals(this.duration)) {
			this.duration = duration;
			calAutoGeneratedValues();
		}
	}

	public Integer getDateLeftForTreatment() {
		return dateLeftForTreatment;
	}

	public Double getTotalAmountInStorage() {
		return totalAmountInStorage;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		if (storage != null && !storage.equals(this.storage)) {
			this.storage = storage;
			calAutoGeneratedValues();
			Double doubleNum = storage.getQuantity() - amountForVetting;
			storage.setQuantity(doubleNum.intValue());
		}
	}
}
