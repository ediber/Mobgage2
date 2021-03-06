package edi.com.mobgage2.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import edi.com.mobgage2.R;
import edi.com.mobgage2.activities.MobgageMainActivity;
import edi.com.mobgage2.managers.DataManager;


public class SimulationInitFragment extends Fragment {


    private View primeBtn;
    private View changingBtn;
    private View IndexBtn;
    private EditText primeEdt;   // BIG
    private EditText changingEdt;
    private EditText IndexEdt;
    private TextView primeTxt;
    private TextView changingTxt;
    private TextView IndexTxt;
    private View calculate;
    private View maxBtn;
    private TextView maxTxt;
    private EditText maxEdt;

    public SimulationInitFragment() {
        // Required empty public constructor
    }


    public static SimulationInitFragment newInstance() {
        SimulationInitFragment fragment = new SimulationInitFragment();

        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_simulation_init, container, false);

        primeBtn =  view.findViewById(R.id.simulation_init_prime_btn);
        changingBtn =  view.findViewById(R.id.simulation_init_changing_btn);
        IndexBtn =  view.findViewById(R.id.simulation_init_index_btn);
        maxBtn =  view.findViewById(R.id.simulation_init_max_btn);
        calculate =  view.findViewById(R.id.simulation_init_calculate);


        primeTxt = (TextView)view.findViewById(R.id.simulation_init_prime_txt);
        changingTxt = (TextView)view.findViewById(R.id.simulation_init_changing_txt);
        IndexTxt = (TextView)view.findViewById(R.id.simulation_init_index_txt);
        maxTxt = (TextView)view.findViewById(R.id.simulation_init_max_txt);

        primeEdt = (EditText)view.findViewById(R.id.simulation_init_prime_edit);
        changingEdt = (EditText)view.findViewById(R.id.simulation_init_changing_edit);
        IndexEdt = (EditText)view.findViewById(R.id.simulation_init_index_edit);
        maxEdt = (EditText)view.findViewById(R.id.simulation_init_max_edit);


        primeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                primeEdt.setText(withoutPercentage(primeTxt.getText()));
            }
        });

        changingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingEdt.setText(withoutPercentage(changingTxt.getText()));
            }
        });

        IndexBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IndexEdt.setText(withoutPercentage(IndexTxt.getText()));
            }
        });

        maxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxEdt.setText(withoutPercentage(maxTxt.getText()));
            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager manager = DataManager.getInstance();
                manager.getSimulationDetails().setBankIsraelAnnualGrowth(Double.parseDouble(primeEdt.getText().toString()));
                manager.getSimulationDetails().setFixedInterestAnnualGrowth(Double.parseDouble(changingEdt.getText().toString()));
                manager.getSimulationDetails().setIndexAnnualGrowth(Double.parseDouble(IndexEdt.getText().toString()));
                manager.getSimulationDetails().setCapInterest(Double.parseDouble(maxEdt.getText().toString()));

                ((MobgageMainActivity) (getActivity())).showScreen(MobgageMainActivity.SCREEN_USER_SIMULATION_COMPARE, true, null);
            }
        });

        return view;
    }

    private String withoutPercentage(CharSequence text) {
        return text.toString().substring(0, text.length()-1);
    }


}
