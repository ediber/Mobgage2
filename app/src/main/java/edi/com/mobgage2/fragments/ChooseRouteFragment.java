package edi.com.mobgage2.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edi.com.mobgage2.R;
import edi.com.mobgage2.activities.MobgageMainActivity;
import edi.com.mobgage2.data.Proposal;
import edi.com.mobgage2.data.RouteKind;
import edi.com.mobgage2.data.Types.RouteKinds;
import edi.com.mobgage2.managers.ActiveSelectionData;
import edi.com.mobgage2.managers.DataManager;
import edi.com.mobgage2.utils.Utils;

public class ChooseRouteFragment extends Fragment implements OnItemClickListener
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_banks, null);
		
		String titleText = null;
		Proposal currentProposal = ActiveSelectionData.getInstance().getCurrentProposal();
		
		titleText = getResources().getString(R.string.choose_route_title) + " " + (currentProposal.getRoutes().size() + 1);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(titleText);
		
		ListView routesKindsList = (ListView)view.findViewById(R.id.list);
		routesKindsList.setAdapter(new DataAdapter(DataManager.getInstance().getAllRoutesKinds()));
		routesKindsList.setOnItemClickListener(this);
		return view;
	}
	
	private class DataAdapter extends ArrayAdapter<RouteKind>
	{      
		public DataAdapter(List<RouteKind> routesKinds)
		{
			super(getActivity(), R.layout.bank_row,
					R.id.text, routesKinds);
		}
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View v = super.getView(position, convertView, parent);
			ViewGroup.LayoutParams params = v.getLayoutParams();
			params.height = params.height+20;
			v.setLayoutParams(params);
			
			TextView text = (TextView) v.findViewById(R.id.text);
			ImageView image = (ImageView) v.findViewById(R.id.image);
			RouteKind kind = getItem(position);
			text.setText(kind.routeKindName);
			text.setVisibility(View.VISIBLE);
			image.setVisibility(View.GONE);
			return v;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		RouteKind kind = (RouteKind) parent.getItemAtPosition(position);
		if(kind.routeKindID == RouteKinds.KIND_DOLAR || kind.routeKindID == RouteKinds.KIND_MAKAM)
		{
			return;
		}

		// only new routes is coming here	
		Proposal proposal = ActiveSelectionData.getInstance().getCurrentProposal();
		
		if(DataManager.getInstance().isProposalHasRouteOfKind(proposal, kind.routeKindID))
		{
			String title = getResources().getString(R.string.alert3_title);
			String msg = getResources().getString(R.string.alert3_msg);
			String buttonText = getResources().getString(R.string.back);
			Utils.showAlert(title, msg, buttonText, getActivity(), null);
		}
		else
		{
			ActiveSelectionData.getInstance().setCurrentRoute(DataManager.getInstance().getNewRouteForProposalID(proposal, kind.routeKindID));

			moveToScreen(MobgageMainActivity.SCREEN_ROUTE_DETAILS, true);
		}
	}
	
	private void moveToScreen(int screen, boolean forward)
	{
		((MobgageMainActivity)(getActivity())).showScreen(screen, forward, null);
	}
}
