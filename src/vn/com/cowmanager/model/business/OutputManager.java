package vn.com.cowmanager.model.business;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Tuple;
import vn.com.cowmanager.model.arrangement.CageManager;

@DClass(schema = "businesss")
public class OutputManager {

	private static Integer idCounter = 0;

	@DAttr(name = "id", type = Type.String, id = true, auto = true, optional = false, mutable = false, length = 6)
	private String id;

	@DAttr(name = "cageForSale", type = Type.Domain, optional = false, length = 6)
	@DAssoc(ascName = "OutputManager-has-cageForSale", ascType = AssocType.One2Many, endType = AssocEndType.Many, role = "OutputManager", associate = @Associate(cardMax = 25, cardMin = 1, type = CageManager.class))
	private CageManager cageForSale;

	@DAttr(name = "customer", type = Type.Domain, optional = false, length = 6)
	@DAssoc(ascName = "OutputManager-has-customer", ascType = AssocType.One2Many, endType = AssocEndType.Many, role = "OutputManager", associate = @Associate(cardMax = 25, cardMin = 1, type = Customer.class))
	private Customer customer;

	@DAttr(name = "numberOfCow", type = Type.Integer, mutable = false, length = 6)
	private Integer numberOfCow;

	@DAttr(name = "avgWeight", type = Type.Double, mutable = false, length = 6)
	private Double avgWeight;

	@DAttr(name = "saleCost", type = Type.Double, length = 6)
	private Double saleCost;

	@DAttr(name = "totalCost", type = Type.Double, mutable = false, length = 6)
	private Double totalCost;

	@DAttr(name = "saleDate", type = Type.String, optional = false, length = 15)
	private String saleDate;

	public OutputManager(String id, CageManager cageForSale, Customer customer, Integer numberOfCow, Double avgWeight,
			Double saleCost, Double totalCost, String saleDate) {
		this.id = nextId(id);
		this.cageForSale = cageForSale;
		this.customer = customer;
		this.saleCost = saleCost;
		if (validateDate(saleDate)) {
			this.saleDate = saleDate;
		} else {
			throw new NotPossibleException(null,
					"Ä�á»‹nh dáº¡ng ngÃ y khÃ´ng há»£p lá»‡, ngÃ y Ä‘Æ°á»£c Ä‘á»‹nh dáº¡ng Ä‘Ãºng theo: dd/MM/yyyy");
		}
		calAutoGeneratedValues();
	}

	public OutputManager(@AttrRef("cageForSale") CageManager cageForSale, @AttrRef("customer") Customer customer,
			@AttrRef("numberOfCow") Integer numberOfCow, @AttrRef("avgWeight") Double avgWeight,
			@AttrRef("saleCost") Double saleCost, @AttrRef("totalCost") Double totalCost,
			@AttrRef("saleDate") String saleDate) {
		this(null, cageForSale, customer, numberOfCow, avgWeight, saleCost, totalCost, saleDate);
	}

	public OutputManager(@AttrRef("cageForSale") CageManager cageForSale, @AttrRef("customer") Customer customer,
			@AttrRef("numberOfCow") Integer numberOfCow, @AttrRef("avgWeight") Double avgWeight,
			@AttrRef("totalCost") Double totalCost, @AttrRef("saleDate") String saleDate) {
		this(null, cageForSale, customer, numberOfCow, avgWeight, null, totalCost, saleDate);
	}

	private String nextId(String id) throws ConstraintViolationException {
		if (id == null) { // generate a new id
			idCounter++;
			return "OM" + idCounter;
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
		calNumberOfCow();
		calAvgWeight();
		calTotalCost();
	}

	public void calNumberOfCow() {
		numberOfCow = cageForSale.getNumberOfCow();
	}

	public void calAvgWeight() {
		avgWeight = cageForSale.getWeightAvg();
	}

	public void calTotalCost() {
		totalCost = numberOfCow * saleCost;
	}

	private boolean validateDate(String date) throws NotPossibleException {
		// 20/11/1997
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
		return "OutputManager: " + id + "," + cageForSale + "," + customer + "," + totalCost + ")";
	}

	public String getId() {
		return id;
	}

	public CageManager getCageForSale() {
		return cageForSale;
	}

	public void setCageForSale(CageManager cageForSale) {
		if (cageForSale != null && !cageForSale.equals(cageForSale)) {
			this.cageForSale = cageForSale;
			calAutoGeneratedValues();
		}
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Integer getNumberOfCow() {
		return numberOfCow;
	}

	public Double getAvgWeight() {
		return avgWeight;
	}

	public Double getSaleCost() {
		return saleCost;
	}

	public void setSaleCost(Double saleCost) {
		this.saleCost = saleCost;
		calAutoGeneratedValues();
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public String getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(String saleDate) {
		this.saleDate = saleDate;
	}

}
