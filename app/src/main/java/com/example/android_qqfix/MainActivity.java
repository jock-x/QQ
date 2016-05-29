package com.example.android_qqfix;

import java.util.ArrayList;
import java.util.HashMap;

import com.Data.DatabaseHelper;
import com.dragon.persondata.*;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends Activity {

	TextView textFetchPassWord=null,textRegister=null;
	Button loginButton=null;


	SQLiteOpenHelper helper;
	ImageButton  listIndicatorButton=null, deleteButtonOfEdit=null;
	ImageView currentUserImage=null;
	ListView loginList=null;
	EditText qqEdit=null, passwordEdit=null;
	private static boolean isVisible=false;         //ListView是否可见
	private static boolean isIndicatorUp=false;     //指示器的方向

	public static int currentSelectedPosition=-1;
	//用于记录当前选择的ListView中的QQ联系人条目的ID，如果是-1表示没有选择任何QQ账户，注意在向
	//List中添加条目或者删除条目时都要实时更新该currentSelectedPosition

	String[] from={"userPhoto","userQQ","deletButton"};
	int[] to={R.id.login_userPhoto,R.id.login_userQQ,R.id.login_deleteButton};
	ArrayList<HashMap<String,Object>> list=null;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
		helper = new DatabaseHelper(this, "User.db", null, 2);
		helper.getWritableDatabase();



		textFetchPassWord=(TextView)findViewById(R.id.fetchPassword);
		textRegister=(TextView)findViewById(R.id.registQQ);
		loginButton=(Button)findViewById(R.id.qqLoginButton);
		listIndicatorButton=(ImageButton)findViewById(R.id.qqListIndicator);
		loginList=(ListView)findViewById(R.id.loginQQList);
		list=new ArrayList<HashMap<String,Object>>();
		currentUserImage=(ImageView)findViewById(R.id.myImage);
		qqEdit=(EditText)findViewById(R.id.qqNum);
		passwordEdit=(EditText)findViewById(R.id.qqPassword);
		deleteButtonOfEdit=(ImageButton)findViewById(R.id.delete_button_edit);


		qqEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(qqEdit.getText().toString().equals("")==false){
					deleteButtonOfEdit.setVisibility(View.VISIBLE);
				}

			}
		});

		deleteButtonOfEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				currentUserImage.setImageResource(R.drawable.qqmain);
				qqEdit.setText("");

			    currentSelectedPosition=-1;
				deleteButtonOfEdit.setVisibility(View.GONE);

			}
		});

		user();
     	/*	UserInfo user1=new UserInfo(R.drawable.contact_0,"1234567","21eqwre",R.drawable.deletebutton);
		UserInfo user2=new UserInfo(R.drawable.contact_1,"10023455","32rewqeaf",R.drawable.deletebutton);
		addUser(user1);
		addUser(user2);
         */
		//设置当前显示的被选中的账户的头像
		if(currentSelectedPosition==-1){
			currentUserImage.setImageResource(R.drawable.qqmain);
			qqEdit.setText("");
		}
		else{
			currentUserImage.setImageResource((Integer)list.get(currentSelectedPosition).get(from[0]));
			qqEdit.setText((String)list.get(currentSelectedPosition).get(from[1]));
		}

		MyLoginListAdapter adapter=new MyLoginListAdapter(this, list, R.layout.layout_list_item, from, to);
		loginList.setAdapter(adapter);
		loginList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				currentUserImage.setImageResource((Integer)list.get(arg2).get(from[0]));
				qqEdit.setText((String)list.get(arg2).get(from[1]));
				currentSelectedPosition=arg2;

				//相应完点击后List就消失，指示箭头反向！
				loginList.setVisibility(View.GONE);
				listIndicatorButton.setBackgroundResource(R.drawable.indicator_down);



			}


		});

		listIndicatorButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isIndicatorUp){
					isIndicatorUp=false;
					isVisible=false;
					listIndicatorButton.setBackgroundResource(R.drawable.indicator_down);
					loginList.setVisibility(View.GONE);   //让ListView列表消失

				}
				else{
					isIndicatorUp=true;
					isVisible=true;
					listIndicatorButton.setBackgroundResource(R.drawable.indicator_up);
					loginList.setVisibility(View.VISIBLE);
				}
			}

		});

		loginButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String name =qqEdit.getText().toString();
				if(name.length()!=0&&!name.equals("")) {
				if(SelectTb(name)){

					InsertTb(name);
					Intent intent = new Intent(MainActivity.this, ContactsListExpandable.class);
					MainActivity.this.startActivity(intent);
				}

					Intent intent = new Intent(MainActivity.this, ContactsListExpandable.class);
					MainActivity.this.startActivity(intent);

				}else {
                 Toast.makeText(MainActivity.this,"请输入用户名",Toast.LENGTH_SHORT).show();

				}


			}

		});

	}


	//继承onTouchEvent方法，用于实现点击控件之外的部分使控件消失的功能




	private void addUser(UserInfo user){
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put(from[0], user.userPhoto);
		map.put(from[1], user.userQQ);
		map.put(from[2], user.deleteButtonRes);

		list.add(map);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction()==MotionEvent.ACTION_DOWN && isVisible){
			int[] location=new int[2];
			//调用getLocationInWindow方法获得某一控件在窗口中左上角的横纵坐标
			loginList.getLocationInWindow(location);
			//获得在屏幕上点击的点的坐标
			int x=(int)event.getX();
			int y=(int)event.getY();
			if(x<location[0]|| x>location[0]+loginList.getWidth() ||
					y<location[1]||y>location[1]+loginList.getHeight()){
				isIndicatorUp=false;
				isVisible=false;


				listIndicatorButton.setBackgroundResource(R.drawable.indicator_down);
				loginList.setVisibility(View.GONE);   //让ListView列表消失，并且让游标向下指！

			}


		}


		return super.onTouchEvent(event);
	}

	public void DelTb(String name){
		SQLiteDatabase sdb = helper.getReadableDatabase();
		String sql = "delete from User where UserName="+name;
		try {
			sdb.execSQL(sql);
		} catch (SQLException e) {
			Log.i("err", "del failed");
		}
	}
	public Boolean SelectTb(String name) {

		SQLiteDatabase sdb = helper.getReadableDatabase();
		String sql = "select * from  User where UserName="+name;
	    boolean r =false;
		try {
			Cursor cursor = sdb.rawQuery(sql,null);
			if (cursor.moveToFirst()) {
					r= false;

				} else {

					r=  true;
				}


			} catch (SQLException e) {
             Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
			}

             return r;

	}





		public void InsertTb(String name){
		if(!name.equals(null)) {
			SQLiteDatabase sdb = helper.getReadableDatabase();
			String sql = "insert into User (UserName,UserPicture) values (?,?)";
			Object obj[] = {name, R.drawable.contact_0};
			try {
				sdb.execSQL(sql, obj);
			} catch (SQLException e) {
				Log.i("err", "insert failed");
			}

		}
	   }


	private void user() {
		//3,数据库的操作，查询
		SQLiteDatabase sdb = helper.getReadableDatabase();
		try {
			String sql = "select * from User";
			// 实现遍历id和name
			Cursor cursor = sdb.rawQuery(sql,null);
			if (cursor.moveToFirst()) {
				do {

					String UserName = cursor.getString(cursor
							.getColumnIndex("UserName"));
					String UserPicture = cursor.getString(cursor
							.getColumnIndex("UserPicture"));

					UserInfo user1=new UserInfo(R.drawable.contact_0,UserName,"12313",R.drawable.deletebutton);

					addUser(user1);



				} while (cursor.moveToNext());
			}
			cursor.close();
			sdb.close();
		} catch (SQLiteException e) {
			Toast.makeText(getApplicationContext(), "查询",
					Toast.LENGTH_SHORT).show();
		}
	}



	/**
	 * 为了便于在适配器中修改登录界面的Activity，这里把适配器作为
	 * MainActivity的内部类，避免了使用Handler，简化代码
	 * @author DragonGN
	 *
	 */






	public class MyLoginListAdapter extends BaseAdapter{

		protected Context context;
		protected ArrayList<HashMap<String,Object>> list;
		protected int itemLayout;
		protected String[] from;
		protected int[] to;




		public MyLoginListAdapter(Context context,
								  ArrayList<HashMap<String, Object>> list, int itemLayout,
								  String[] from, int[] to) {
			super();
			this.context = context;
			this.list = list;
			this.itemLayout = itemLayout;
			this.from = from;
			this.to = to;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		class ViewHolder{
			public ImageView userPhoto;
			public TextView userQQ;
			public ImageButton deleteButton;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder=null;
			/*
			currentPosition=position;
			不能使用currentPosition，因为每绘制完一个Item就会更新currentPosition
			这样得到的currentPosition将始终是最后一个Item的position
			*/

			if(convertView==null){
				convertView=LayoutInflater.from(context).inflate(itemLayout, null);
				holder=new ViewHolder();
				holder.userPhoto=(ImageView)convertView.findViewById(to[0]);
				holder.userQQ=(TextView)convertView.findViewById(to[1]);
				holder.deleteButton=(ImageButton)convertView.findViewById(to[2]);
				convertView.setTag(holder);
			}
			else{
				holder=(ViewHolder)convertView.getTag();
			}

			holder.userPhoto.setBackgroundResource((Integer)list.get(position).get(from[0]));
			holder.userQQ.setText((String)list.get(position).get(from[1]));
			holder.deleteButton.setBackgroundResource((Integer)list.get(position).get(from[2]));
			holder.deleteButton.setOnClickListener(new ListOnClickListener(position));

			return convertView;
		}

		class ListOnClickListener implements OnClickListener{

			private int position;


			public ListOnClickListener(int position) {
				super();
				this.position = position;
			}

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String name= list.get(position).get(from[1]).toString();
				DelTb(name);
				list.remove(position);

				//如果删除的就是当前显示的账号，那么将主界面当前显示的头像设置回初始头像
				if(position==currentSelectedPosition){
					currentUserImage.setImageResource(R.drawable.qqmain);
					qqEdit.setText("");
					currentSelectedPosition=-1;
				}
				else if(position<currentSelectedPosition){
					currentSelectedPosition--;    //这里小于当前选择的position时需要进行减1操作
				}

				listIndicatorButton.setBackgroundResource(R.drawable.indicator_down);
				loginList.setVisibility(View.GONE);   //让ListView列表消失，并且让游标向下指！

				MyLoginListAdapter.this.notifyDataSetChanged();


			}

		}


	}




}
