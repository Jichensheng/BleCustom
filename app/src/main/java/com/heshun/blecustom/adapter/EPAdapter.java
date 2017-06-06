package com.heshun.blecustom.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heshun.blecustom.R;
import com.heshun.blecustom.entity.ElectricityParameter;

import java.util.List;

/**
 * 电力参数列表
 * author：Jics
 * 2017/3/24 12:49
 */
public class EPAdapter extends RecyclerView.Adapter<EPAdapter.ViewsHolder> {

	private Context context;
	private List<ElectricityParameter> eps;

	public EPAdapter(Context context, List<ElectricityParameter> eps) {
		this.context = context;
		this.eps=eps;
	}

	@Override
	public ViewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewsHolder(LayoutInflater.from(context).inflate(R.layout.ep_item, parent, false));
	}

	@Override
	public void onBindViewHolder(ViewsHolder holder, int position) {
		holder.name.setText(eps.get(position).getName());
		holder.value.setText(eps.get(position).getValue());
		holder.unit.setText(eps.get(position).getUnit());
	}

	@Override
	public int getItemCount() {
		return eps.size();
	}

	class ViewsHolder extends RecyclerView.ViewHolder {
		TextView name,value,unit;
		ViewsHolder(View itemView) {
			super(itemView);
			name= (TextView) itemView.findViewById(R.id.ep_name);
			value= (TextView) itemView.findViewById(R.id.ep_value);
			unit= (TextView) itemView.findViewById(R.id.ep_uint);
		}
	}
}
