package ir.atriatech.pizzawifi.ui.main.more.requestForm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.skydoves.powermenu.MenuBaseAdapter;

import ir.atriatech.pizzawifi.R;

public class RequestFormSituationAdapter extends MenuBaseAdapter<SituationModel> {

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.temp_request_situation, viewGroup, false);
        }

        SituationModel item = (SituationModel) getItem(index);
        final AppCompatTextView title = view.findViewById(R.id.txtName);
//        final AppCompatImageView imgCheck = view.findViewById(R.id.imgCheck);
        final ConstraintLayout container = view.findViewById(R.id.container);

        title.setText(item.getName());
        if (item.getSelected()) {
//            imgCheck.setVisibility(View.VISIBLE);
            title.setTextColor(ContextCompat.getColor(context, R.color.black));
            container.setBackgroundColor(ContextCompat.getColor(context, R.color.mercury));
        } else {
//            imgCheck.setVisibility(View.INVISIBLE);
            title.setTextColor(ContextCompat.getColor(context, R.color.black));
            container.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
        return super.getView(index, view, viewGroup);
    }
}