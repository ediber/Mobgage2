package edi.com.mobgage2.enums;


import edi.com.mobgage2.R;
import edi.com.mobgage2.managers.DataManager;

public enum EReturnMethod
{
	EQUAL_FOUNDATION(0), SHPITZER(1);
	
	private int intValue;
	private EReturnMethod(int value)
	{
		intValue = value;
	}
	public static EReturnMethod toEReturnMethod(int value)
	{
		switch (value) 
		{
		case 0:
		{
			return EQUAL_FOUNDATION;
		}
		case 1:
		{
			return SHPITZER;
		}
		default:
			return null;
		}
	}
	public String stringValue()
	{
		switch (intValue) 
		{
		case 0:
		{
			return DataManager.getInstance().getStringResource(R.string.equal_foundation);
		}
		case 1:
		{
			return DataManager.getInstance().getStringResource(R.string.shpitzer);
		}
		default:
			return null;
		}
	}
	
	public int getIntValue()
	{
		return intValue;
	}
}
