package com.example.android_qqfix;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
/*import android.support.v4.widget.SwipeRefreshLayout;*/
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import com.Data.DatabaseHelper;
import com.Data.DatabaseService;
import com.dragon.persondata.UserInfo;
import com.http.JuheDemo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.Gson;
import Gson.First;


public class ChatActivity extends Activity implements OnRefreshListener{

	SQLiteOpenHelper helper;
	HashMap<String,Integer> faceMap=null;
	ArrayList<HashMap<String,Object>> chatList=null;
	String[] from={"image","text","time"};
	int[] to={R.id.chatlist_image_me,R.id.chatlist_text_me,R.id.chatlist_timeme,R.id.chatlist_image_other,R.id.chatlist_text_other,R.id.chatlist_timeother};
	int[] layout={R.layout.chat_listitem_me,R.layout.chat_listitem_other};
	String userQQ=null;
       int  count =0;

    //下拉刷新
	SwipeRefreshLayout Refresh;
	private boolean isRefresh = false;

	// ListView头部下拉刷新的布局

	/**
	 * 这里两个布局文件使用了同一个id，测试一下是否管用
	 * TT事实证明这回产生id的匹配异常！所以还是要分开。。
	 *
	 * userQQ用于接收Intent传递的qq号，进而用来调用数据库中的相关的联系人信息，这里先不讲
	 * 先暂时使用一个头像
	 */

	public final static int OTHER=1;
	public final static int ME=0;
	public final static int ActivityID=0;
	private int visibleLastIndex = 0;   //最后的可视项索引
	private int visibleItemCount;       // 当前窗口可见项总数
	private Boolean r = false;
	protected ListView chatListView=null;
	protected Button chatSendButton=null;
	protected EditText editText=null;

	protected ImageButton chatBottomLook=null;
	protected RelativeLayout faceLayout=null;
	protected TabHost tabHost=null;
	protected TabWidget tabWidget=null;


	private boolean expanded=false;
      private String myword;
	protected MyChatAdapter adapter=null;

	private Handler handler = new Handler();
	private View loadMoreView;
	private Button loadMoreButton;
	private ImageView mHeaderImageView;

	private DatabaseService service;
	private int currentPage = 0; //默认在第一页
	private static final int lineSize =10;    //每次显示数
	private int allRecorders = 0;  //全部记录数
	private int pageSize = 1;  //默认共一页
	private ArrayList<String> items;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);


		chatList=new ArrayList<HashMap<String,Object>>();
		helper = new DatabaseHelper(this, "User.db", null, 2);
		helper.getWritableDatabase();
		service = new DatabaseService(this);
        chatSendButton=(Button)findViewById(R.id.chat_bottom_sendbutton);
		editText=(EditText)findViewById(R.id.chat_bottom_edittext);
		chatListView=(ListView)findViewById(R.id.chat_list);
		tabWidget=(TabWidget)findViewById(android.R.id.tabs);
		tabHost=(TabHost)findViewById(android.R.id.tabhost);

		allRecorders = service.getCount();
			//计算总页数
		pageSize = (allRecorders + lineSize - 1) / lineSize;
	   	items = service.getAllItems(currentPage, lineSize);
		Collections.reverse(items);  //倒序


		addTextToList("你是谁", ME);
		addTextToList("我是聊天机器人\n  ^_^", OTHER);
		/*user();*/

		chatBottomLook=(ImageButton)findViewById(R.id.chat_bottom_look);
		faceLayout=(RelativeLayout)findViewById(R.id.faceLayout);

		Refresh = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
		Refresh.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_red_light,
				android.R.color.holo_orange_light);
		Refresh.setOnRefreshListener(this);

		/**
		 * 添加选项卡
		 */


		/**
		 * 读取全部数据
		 */


		chatBottomLook.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(expanded){

					expanded=false;


					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

					/**height不设为0是因为，希望可以使再次打开时viewFlipper已经初始化为第一页 避免
					 *再次打开ViewFlipper时画面在动的结果,
					 *为了避免因为1dip的高度产生一个白缝，所以这里在ViewFlipper所在的RelativeLayout
					 *最上面添加了一个1dip高的黑色色块
					 */


				}
				else{

					expanded=true;


				}
			}

		});

		/**EditText从未获得焦点到首次获得焦点时不会调用OnClickListener方法，所以应该改成OnTouchListener
		 * 从而保证点EditText第一下就能够把表情界面关闭
		 editText.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
		// TODO Auto-generated method stub
		ViewGroup.LayoutParams params=viewFlipper.getLayoutParams();
		params.height=0;
		viewFlipper.setLayoutParams(params);
		expanded=false;
		System.out.println("WHYWHWYWHYW is Clicked");
		}

		});
		 **/
		editText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(expanded){


					expanded=false;
				}
				return false;
			}
		});
		adapter=new MyChatAdapter(this,chatList,layout,from,to);
		chatSendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String myWord=null;

				/**
				 * 这是一个发送消息的监听器，注意如果文本框中没有内容，那么getText()的返回值可能为
				 * null，这时调用toString()会有异常！所以这里必须在后面加上一个""隐式转换成String实例
				 * ，并且不能发送空消息。
				 */

				myword=(editText.getText()+"").toString();
				InsertTb(myword,0);
				if(myword.length()==0)
					return;
				editText.setText("");
				addTextToList(myword, ME);

				final Handler myHandler=new Handler(){
					public void handleMessage(Message msg) {

						try{
							Gson mgson = new Gson();
							First code = mgson.fromJson(
									msg.obj.toString(), First.class);
							addTextToList(code.getResult().getText().toString(),OTHER);
							InsertTb(code.getResult().getText().toString(),1);

						}catch(Exception e){
							Toast.makeText(ChatActivity.this, "消息发送失败",Toast.LENGTH_LONG).show();
						}

					}
					};
				new Thread(){
					public void run() {
						JuheDemo jiqiren = new JuheDemo();
						String response = jiqiren.getRequest1(myword);
						Message message=new Message();
						message.obj =response;
						myHandler.sendMessage(message);
					}
				}.start();

				/**
				 * 更新数据列表，并且通过setSelection方法使ListView始终滚动在最底端
				 */
				adapter.notifyDataSetChanged();
				chatListView.setSelection(chatList.size()-1);



			}
		});




		chatListView.setAdapter(adapter);
		chatListView.setSelection(items.size());//直接定位到最底部
		chatListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub

				((InputMethodManager)ChatActivity.this.getSystemService(INPUT_METHOD_SERVICE)).
						hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				expanded=false;
			}
		});

		/**
		 * 为表情Map添加数据
		 */
}







	/**
	 * 滑动状态改变时被调用
	 */




	/**
	 * 模拟加载数据
	 */
	public void onRefresh() {
		if(!isRefresh){
			isRefresh = true;
			currentPage++;
			r=true;
			appendDate();
					new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Refresh.setRefreshing(false);
					chatListView.setAdapter(adapter);
					if (currentPage < pageSize){

						chatListView.setSelection(5);
					}

					isRefresh = false;
				}
			}, 2000);
		}
	}





	private void appendDate(){
		final  ArrayList<String> additems = service.getAllItems(currentPage, lineSize);

		adapter.setCount(adapter.getCount() + additems.size());
		//判断，如果到了最末尾则去掉“正在加载”
       try {
           for(int q=0;q<additems.size()/3;q++) {
			 String ChatButtom = additems.get(q*3+0);
			 String time = additems.get(q*3+1);
			 String three = additems.get(q*3+2);
			 int i = Integer.parseInt(three);
			 if (i == 0) {

				 addTextToListtime(ChatButtom, ME, time);
			 } else {
				 addTextToListtime(ChatButtom, OTHER, time);
			 }
		 }

		   }catch (Exception e){

		   }
	}

	public void InsertTb(String ChatButtom,int whos){
		SQLiteDatabase sdb = helper.getReadableDatabase();


		String sql = "insert into Chat (ChatButtom,ChatWho) values (?,?)";

		Object obj[]={ChatButtom,whos};
		try {
			sdb.execSQL(sql, obj);
		} catch (SQLException e) {
			Log.i("err", "insert failed");
		}
	}



	public void DelTb(String ChatButtom){
		SQLiteDatabase sdb = helper.getReadableDatabase();


		String sql = "delete  from Chat where ChatButtom = ?";


		try {
			sdb.execSQL(sql, new String[] {ChatButtom});
		} catch (SQLException e) {
			Log.i("err", "insert failed");
		}
	}


	private void user() {
		//3,数据库的操作，查询
		SQLiteDatabase sdb = helper.getReadableDatabase();
		try {


			String sql = "select * from Chat order by ChatID desc";
			// 实现遍历id和name

			Cursor cursor = sdb.rawQuery(sql,null);
			if (cursor.moveToFirst()) {
				do {

					String ChatButtom = cursor.getString(cursor
							.getColumnIndex("ChatButtom"));
					int Who = cursor.getInt(cursor
							.getColumnIndex("ChatWho"));

					String time = cursor.getString(cursor
							.getColumnIndex("ChatData"));
                    if(Who==0) {
						addTextToListtime(ChatButtom, ME,time);

					}else{
						addTextToListtime(ChatButtom,OTHER,time);


					}


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
	 * 打开或者关闭软键盘，之前若打开，调用该方法后关闭；之前若关闭，调用该方法后打开
	 */

	private void setSoftInputState(){
		((InputMethodManager)ChatActivity.this.getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}



	private void setFaceLayoutExpandState(boolean isexpand){
		if(isexpand==false){


			ViewGroup.LayoutParams params=faceLayout.getLayoutParams();
			params.height=1;
			faceLayout.setLayoutParams(params);

			chatBottomLook.setBackgroundResource(R.drawable.chat_bottom_look);
			Message msg=new Message();
			msg.what=this.ActivityID;
			msg.obj="collapse";

			Message msg2=new Message();
			msg2.what=this.ActivityID;
			msg2.obj="collapse";


			chatListView.setSelection(chatList.size()-1);//使会话列表自动滑动到最低端

		}
		else{

			((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
					(ChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			ViewGroup.LayoutParams params=faceLayout.getLayoutParams();
			params.height=185;
			//	faceLayout.setLayoutParams(new RelativeLayout.LayoutParams( ));
			RelativeLayout.LayoutParams relativeParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			faceLayout.setLayoutParams(relativeParams);


			chatBottomLook.setBackgroundResource(R.drawable.chat_bottom_keyboard);

		}
	}


	private void setFaceText(TextView textView,String text){
		SpannableString spanStr=parseString(text);
		textView.setText(spanStr);
	}



	private SpannableString parseString(String inputStr){
		SpannableStringBuilder spb=new SpannableStringBuilder();
		Pattern mPattern= Pattern.compile("\\\\..");
		Matcher mMatcher=mPattern.matcher(inputStr);
		String tempStr=inputStr;

		while(mMatcher.find()){
			int start=mMatcher.start();
			int end=mMatcher.end();
			spb.append(tempStr.substring(0,start));
			String faceName=mMatcher.group();
			setFace(spb, faceName);
			tempStr=tempStr.substring(end, tempStr.length());
			/**
			 * 更新查找的字符串
			 */
			mMatcher.reset(tempStr);
		}
		spb.append(tempStr);
		return new SpannableString(spb);
	}


	private void setFace(SpannableStringBuilder spb, String faceName){
		Integer faceId=faceMap.get(faceName);
		if(faceId!=null){
			Bitmap bitmap=BitmapFactory.decodeResource(getResources(), faceId);
			bitmap=Bitmap.createScaledBitmap(bitmap, 30, 30, true);
			ImageSpan imageSpan=new ImageSpan(this,bitmap);
			SpannableString spanStr=new SpannableString(faceName);
			spanStr.setSpan(imageSpan, 0, faceName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			spb.append(spanStr);
		}
		else{
			spb.append(faceName);
		}

	}





	protected void addTextToList(String text, int who){
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("person",who);
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
		map.put("time",time);
        map.put("image", who==ME?R.drawable.contact_0:R.drawable.contact_1);
		map.put("text", text);
		chatList.add(map);
	}


	protected void addTextToListtime(String text, int who,String time){
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("person",who);
		map.put("time",time);
		map.put("image", who==ME?R.drawable.contact_0:R.drawable.contact_1);
		map.put("text", text);
		chatList.add(0,map);

	}






	protected void clearList(String text, int who){
         chatList.clear();
	}



	private class MyChatAdapter extends BaseAdapter{

		Context context=null;
		ArrayList<HashMap<String,Object>> chatList=null;
		int[] layout;
		String[] from;
		int[] to;

		int count = lineSize;

		public MyChatAdapter(Context context,
							 ArrayList<HashMap<String, Object>> chatList, int[] layout,
							 String[] from, int[] to) {
			super();
			this.context = context;
			this.chatList = chatList;
			this.layout = layout;
			this.from = from;
			this.to = to;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return chatList.size();
		}

		public void setCount(int count){
			this.count = count;
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
			public TextView DataView=null;
			public ImageView imageView=null;
			public TextView textView=null;

		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder=null;
			int who=(Integer)chatList.get(position).get("person");

			convertView= LayoutInflater.from(context).inflate(
					layout[who==ME?0:1], null);
			holder=new ViewHolder();

			holder.imageView=(ImageView)convertView.findViewById(to[who*3+0]);
			holder.textView=(TextView)convertView.findViewById(to[who*3+1]);
			holder.DataView=(TextView)convertView.findViewById(to[who*3+2]);
			holder.textView.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new AlertDialog.Builder(context).setTitle("删除提示框").setMessage("确认删除该数据？")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {


								String buttom=chatList.get(position).get(from[1]).toString();
                                DelTb(buttom);


									chatList.remove(position);
									adapter.notifyDataSetChanged();
									chatListView.setSelection(position);



								}})
							.setNegativeButton("取消",null)
							.show();


						/*	Toast.makeText(context, +" is clicked", Toast.LENGTH_SHORT).show();*/

				}





			});

			holder.imageView.setBackgroundResource((Integer)chatList.get(position).get(from[0]));
			setFaceText(holder.textView, chatList.get(position).get(from[1]).toString());
			setFaceText(holder.DataView, chatList.get(position).get(from[2]).toString());
			return convertView;
		}

	}



}
