package com.heshun.blecustom.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heshun.blecustom.R;
import com.heshun.blecustom.entity.CMDItem;

import java.util.List;

/**
 * author：Jics
 * 2017/6/5 16:32
 */
public class CMDListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	public static final int ITEM_CAN_JUMP = 0;
	public static final int ITEM_DISABLED_JUMP = 1;
	private CmdItemClickListener cmdItemClickListener;

	private Context context;
	private List<CMDItem> lists;

	public CMDListAdapter(Context context, List<CMDItem> lists) {
		this.context = context;
		this.lists = lists;
	}

	public interface CmdItemClickListener {
		void onCmdClick(boolean isJump,byte cmd);
	}

	public void setCmdItemClickListener(CmdItemClickListener cmdItemClickListener){
		this.cmdItemClickListener=cmdItemClickListener;
	}
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		switch (viewType) {
			case ITEM_CAN_JUMP:
				return new CMDListHolder(LayoutInflater.from(context).inflate(R.layout.cmd_item_jump,null));
			default:
				return new CMDListHolder(LayoutInflater.from(context).inflate(R.layout.cmd_item_no_jump,null));
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof CMDListHolder) {
			((CMDListHolder) holder).textView.setText(lists.get(position).getCmdName());
		} else if (holder instanceof CMDListCanJumpHolder) {
			((CMDListCanJumpHolder) holder).textView.setText(lists.get(position).getCmdName());
		}
	}

	@Override
	public int getItemCount() {
		return lists.size();
	}

	@Override
	public int getItemViewType(int position) {
		if (lists.get(position).getItmeType()) {
			return ITEM_CAN_JUMP;
		} else {
			return ITEM_DISABLED_JUMP;
		}
	}

	private class CMDListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		TextView textView;
		CMDListHolder(View itemView) {
			super(itemView);
			itemView.setOnClickListener(this);
			textView = (TextView) itemView.findViewById(R.id.tv_cmd);
		}

		@Override
		public void onClick(View item) {
			cmdItemClickListener.onCmdClick(lists.get(getAdapterPosition()).getItmeType(),lists.get(getAdapterPosition()).getCmd());
		}
	}

	private class CMDListCanJumpHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		TextView textView;

		CMDListCanJumpHolder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.tv_cmd);
		}

		@Override
		public void onClick(View v) {
			int position=getAdapterPosition();//获取当前位置
			cmdItemClickListener.onCmdClick(lists.get(position).getItmeType(),lists.get(position).getCmd());
		}
	}
}
