package edi.com.mobgage2.data;

public class Route 
{
	public String userID;
	public String proposalID;
	public int routeNum;
	public float loanAmount;
	public int years;
	public float interest;
	public float monthRepayment;
	public int returnMethod; // 1- spitzer, 0- keren shava
	
	public int routeKind;
	public float totalRepayment;
	public int changeYears;

	public Route(String userID, String proposalID, int routeNum, float loanAmount,
				 int years, float interest, float monthRepayment, int returnMethod,
				 int routeKind, float totalRepayment, int changeYears)
	{
		this.userID = userID;
		this.proposalID = proposalID;
		this.routeNum = routeNum;
		this.loanAmount = loanAmount;
		this.years = years;
		this.interest = interest;
		this.monthRepayment = monthRepayment;
		this.returnMethod = returnMethod;
		this.routeKind = routeKind;
		this.totalRepayment = totalRepayment;
		this.changeYears = changeYears;
	}



	public Route copyRoute()
	{
		return new Route(userID, proposalID, routeNum, loanAmount, years,
				interest, monthRepayment, returnMethod, routeKind, totalRepayment, changeYears);
		
	}

	public String getUserID() {
		return userID;
	}

	public String getProposalID() {
		return proposalID;
	}

	public int getRouteNum() {
		return routeNum;
	}

	public float getLoanAmount() {
		return loanAmount;
	}

	public int getYears() {
		return years;
	}

	public float getInterest() {
		return interest;
	}

	public float getMonthRepayment() {
		return monthRepayment;
	}

	public int getReturnMethod() {
		return returnMethod;
	}

	public int getRouteKind() {
		return routeKind;
	}

	public float getTotalRepayment() {
		return totalRepayment;
	}
}
