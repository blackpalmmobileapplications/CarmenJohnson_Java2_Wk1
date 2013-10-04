/*
 * project iShareVegas
 * 
 * package com.CarmenJohnson_Wk2.isharevegas
 * 
 * @author Carmen Johnson
 * 
 * date Sep 12, 2013
 * 
 */
package com.CarmenJohnson_Wk2.isharevegas;

import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.CarmenJohnson.library.DataStorage;
import com.CarmenJohnson.library.Form;
import com.CarmenJohnson.library.ValueLabelPair;
//import com.mynet.NetConnector;

public class MainActivity extends Activity {
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 //currency list to get rates
		 final Spinner spinnerFrom = (Spinner)findViewById(R.id.SpinnerFromCurrency);
		 Form.fillSpinnerWithCurrency(MainActivity.this, spinnerFrom);
		 
		 //list view displaying results
		 final ListView lv = (ListView)findViewById(R.id.listViewRates);
		 
		 //text view to show general/error messages
		 final TextView textViewMessage = (TextView)findViewById(R.id.TextViewMessage);
		 
		 //button to get api data
		 final Button convertButton = (Button)findViewById(R.id.buttonConvert);
		 convertButton.setOnClickListener(new View.OnClickListener() {
			
			//handling click on "Get Latest Exchange Rates Button"
			@Override
			public void onClick(View v) {
				//Get Data
				
				//shows request is processing
				convertButton.setText("Loading...");
				
				
				try{
					
					final String currencyFrom = ((ValueLabelPair)spinnerFrom.getSelectedItem()).getValue();
					
					//if net is not ready 			
					if(!NetConnector.isNetworkConnected(MainActivity.this))
					{
						String message = "Network connection error!.";
						//get data from the file with name of currency
						String[] set = DataStorage.getData(currencyFrom, MainActivity.this);
						if(set.length>0)
						{
							message += "\n Showing lastly fetched data:\n";
							lv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, set));
						}
						textViewMessage.setText(message);
						convertButton.setText("Get Latest Exchange Rates");
						return;
					}
				
					//have used Yahoo finance api to get data
					final String url = "http://query.yahooapis.com/v1/public/yql?q="+URLEncoder.encode("select * from yahoo.finance.xchange where pair in ('"+currencyFrom+"EUR', '"+currencyFrom+"JPY', '"+currencyFrom+"BGN', '"+currencyFrom+"CZK', '"+currencyFrom+"DKK', '"+currencyFrom+"GBP')", "utf-8")+"&env=store://datatables.org/alltableswithkeys&format=json";
					Thread thread = new Thread(new Runnable(){
					    @Override
					    public void run() {
					        try {
					        	//using the user library function to get json data
//My reminder to check my JSONObject	        	
					        	final JSONObject jo = NetConnector.getJSON(MainActivity.this, url).getJSONObject("query").getJSONObject("results");
					        	final JSONArray rates = jo.getJSONArray("rate");
					      
					        	runOnUiThread(new Runnable(){

									@Override
									public void run() {
										try
										{
											//in case of error
											if(jo.has("error") && !jo.getString("error").equals("0") && !jo.getString("error").isEmpty())
											{
												textViewMessage.setText("Some exception has occured");
												//clearing result list view
												lv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, new String[0]));
											}
											//no error
											else
											{
												String[] rate_strs = new String[rates.length()];
												//declaring message
												textViewMessage.setText("");
												
												for(int i=0;i<rates.length();i++)
												{
													//getting two column
													rate_strs[i] = rates.getJSONObject(i).getString("Name")+": "+rates.getJSONObject(i).getString("Rate");													
												}
												//storing result
												DataStorage.storeData(currencyFrom, rate_strs, MainActivity.this);
												//showing result
												lv.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, rate_strs));
											}
											//finished processing request
											convertButton.setText("Get Latest Exchange Rates");
										}
										catch(Exception ex)
										{
											//finished processing request with error/exception
											convertButton.setText("Get Latest Exchange Rates");
										}
									}
					        			
					        	});
								
					        } catch (Exception e) {
					            e.printStackTrace();
					        }
					    }
					});

					thread.start(); 
					
					
					
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
}
