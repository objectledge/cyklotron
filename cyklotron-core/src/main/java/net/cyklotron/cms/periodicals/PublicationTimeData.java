package net.cyklotron.cms.periodicals;

/**
 * Provides default values and state keeping for publication time.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: PublicationTimeData.java,v 1.1 2005-01-12 20:45:08 pablo Exp $
 */
public class PublicationTimeData
{
    public PublicationTimeData(int dayOfMonth, int dayOfWeek, int hour)
    {
		this.dayOfMonth = dayOfMonth; 
		this.dayOfWeek = dayOfWeek;
		this.hour = hour;
    }

    private int dayOfMonth;
	private int dayOfWeek;
	private int hour;

	/**
	 * Returns the value of the <code>day_of_month</code> attribute.
	 *
	 * @return the value of the the <code>day_of_month</code> attribute.
	 * @throws IllegalStateException if the value of the attribute is 
	 *         undefined.
	 */
	public int getDayOfMonth()
	{
		return dayOfMonth;
	}

	/**
	 * Sets the value of the <code>day_of_month</code> attribute.
	 *
	 * @param value the value of the <code>day_of_month</code> attribute.
	 */
	public void setDayOfMonth(int value)
	{
		dayOfMonth = value;
	}

	/**
	 * Returns the value of the <code>day_of_week</code> attribute.
	 *
	 * @return the value of the the <code>day_of_week</code> attribute.
	 * @throws IllegalStateException if the value of the attribute is 
	 *         undefined.
	 */
	public int getDayOfWeek()
	{
		return dayOfWeek;
	}

	/**
	 * Sets the value of the <code>day_of_week</code> attribute.
	 *
	 * @param value the value of the <code>day_of_week</code> attribute.
	 */
	public void setDayOfWeek(int value)
	{
		dayOfWeek = value;
	}
   
	/**
	 * Returns the value of the <code>hour</code> attribute.
	 *
	 * @return the value of the the <code>hour</code> attribute.
	 * @throws IllegalStateException if the value of the attribute is 
	 *         undefined.
	 */
	public int getHour()
	{
		return hour;
	}

	/**
	 * Sets the value of the <code>hour</code> attribute.
	 *
	 * @param value the value of the <code>hour</code> attribute.
	 */
	public void setHour(int value)
	{
		hour = value;
	}
}
