package com.crazyks.mt;

import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.crazyks.mt.MT.Puzzle;

public class Gogogo extends Activity {
    private final String TAG = "Go";
    private Handler mZygote;
    private GridView mGrid;
    private ProgressBar mProgressBar;
    private ImageButton mRight;
    private ImageButton mWrong;
    private Button mNext;
    private boolean runningFlag = false;
    private boolean isStudyMode = false;
    
    private int[] res = { R.drawable.mine_around_0, R.drawable.mine_around_1,
            R.drawable.mine_around_2, R.drawable.mine_around_3,
            R.drawable.mine_around_4, R.drawable.mine_around_5,
            R.drawable.mine_around_6, R.drawable.mine_around_7,
            R.drawable.mine_around_8, R.drawable.mine_exploded,
            R.drawable.mine_common, R.drawable.block_close,
            R.drawable.block_flagged, R.drawable.block_mark, R.drawable.wall, R.drawable.block_safety };
    
    private String mode = null;
    private int timeout = 0;
    private int round = 0;
    private int condition = 0;
    private boolean isEnhanced = false;
    private boolean isPauseOnError = false;
    private boolean isMarkIt = false;
    
    private int rightCounter = 0;
    private int wrongCounter = 0;
    private int missedCounter = 0;
    private int roundCounter = 0;
    
    private CellAdapter mCellAdapter = null;
    private float scale = 1.0f;
    
    private int num1RightCounter = 0;
    private int num2RightCounter = 0;
    private int num3RightCounter = 0;
    private int num4RightCounter = 0;
    private int num5RightCounter = 0;
    private int num6RightCounter = 0;
    private int num7RightCounter = 0;
    private int num8RightCounter = 0;
    private int solvedCounter = 0;
    
    private boolean isSolving = false;
    
    private View mInfoPanel = null;
    private TextView mInfoMode = null;
    private TextView mInfoRounds = null;
    private TextView mInfoEnhanced = null;
    private TextView mInfoMark = null;
    private TextView mInfoCurrentRound = null;
    private TextView mInfoCenterSolved = null;
    private TextView mInfoBoardSolved = null;
    private TextView mInfoUnsolvedBlks = null;
    private TextView mInfoUserChoice = null;
    private TextView mInfoUserMark = null;
    private TextView mInfoCorrect = null;
    private TextView mInfoWrong = null;
    private TextView mInfoMissed = null;
    private ToggleButton mToggleButton = null;
    
    private void increaseCenterRightCounter(int witch) {
    	switch (witch) {
		case 1:
			num1RightCounter++;
			break;
		case 2:
			num2RightCounter++;
			break;
		case 3:
			num3RightCounter++;
			break;
		case 4:
			num4RightCounter++;
			break;
		case 5:
			num5RightCounter++;
			break;
		case 6:
			num6RightCounter++;
			break;
		case 7:
			num7RightCounter++;
			break;
		case 8:
			num8RightCounter++;
			break;
		default:
			break;
		}
    }
    
    private class Score implements Runnable {
    	private static final int SCORE_RIGHT = 0;
    	private static final int SCORE_WRONG = 1;
    	private static final int SCORE_MISSED = 2;
        
        private int scoreType = SCORE_MISSED;
        
        private Score(int type) {
            this.scoreType = type;
        }

        @Override
        public void run() {
            if (!runningFlag) {
                return;
            }
            roundCounter++;
            if (scoreType == SCORE_RIGHT) {
                rightCounter++;
            } else {
                wrongCounter++;
                if (scoreType == SCORE_MISSED) {
                	missedCounter++;
                }
            }
            mProgressBar.setProgress(roundCounter);
            if (roundCounter == round
                    || (wrongCounter * 100 / round) >= condition) {
                showSummary();
            } else {
                if (isStudyMode) {
                    mNext.setEnabled(true);
                    mNext.setText(R.string.press_me);
                    mNext.setTag(TAG_STUDY);
                    mNext.setVisibility(View.VISIBLE);
                    mInfoCurrentRound.setText(String.valueOf(roundCounter));
                    mInfoCenterSolved.setText(String.valueOf(currentRunner.puzzle.isCenterClear()));
                    mInfoBoardSolved.setText(String.valueOf(currentRunner.puzzle.isBoardClear()));
                    mInfoUnsolvedBlks.setText(formatPositions(currentRunner.puzzle.getClosedSafetyBlkPositions()));
                    mInfoUserChoice.setText(currentRunner.userChoice ? "Solved" : "Unsolved");
                    ArrayList<Integer> markedList = new ArrayList<Integer>();
                    for (int i = 0; i < mCellAdapter.states.length; i++) {
                        if (mCellAdapter.states[i] == MT.RESID_SAFETY) {
                            markedList.add(i);
                        }
                    }
                    mInfoUserMark.setText(formatPositions(markedList));
                    mInfoCorrect.setText(String.valueOf(rightCounter));
                    mInfoWrong.setText(String.valueOf(wrongCounter - missedCounter));
                    mInfoMissed.setText(String.valueOf(missedCounter));
                    if (mToggleButton.isChecked()) {
                        mInfoPanel.setVisibility(View.VISIBLE);
                    }
                } else {
                    go();
                }
            }
        }
        
    }
    
    private String formatPositions(ArrayList<Integer> posList) {
        if (posList == null || posList.size() == 0) {
            return "None";
        }
        StringBuffer sb = new StringBuffer();
        for (Integer i : posList) {
            sb.append("(");
            sb.append(i % MT.BOARD_EDGE_WITH_WALL_SIZE);
            sb.append(", ");
            sb.append(i / MT.BOARD_EDGE_WITH_WALL_SIZE);
            sb.append(") ");
        }
        return sb.toString().trim();
    }
    
    private void showSummary() {
        StringBuffer msgBuffer = new StringBuffer();
        msgBuffer.append(getString(R.string.mode) + formatMode(mode) + (isEnhanced ? '(' + getString(R.string.enhanced_mode) + ')' : "") + (isMarkIt ? '(' + getString(R.string.mark) + ')' : "") + '\n');
        if (!isStudyMode) {
            msgBuffer.append(getString(R.string.timeout) + timeout + getString(R.string.timeunit) + '\n');
        }
        msgBuffer.append(getString(R.string.round) + round + '\n');
        msgBuffer.append(getString(R.string.competition) + String.format("%.2f", (roundCounter * 100.0 / round)) + '%' + '\n');
        msgBuffer.append(getString(R.string.right_summary) + String.format("%d (%.2f%%)", rightCounter, (rightCounter * 100.0 / round)) + '\n');
        msgBuffer.append(getString(R.string.wrong_summary) + String.format("%d (%.2f%%, %s %d %s)", wrongCounter, (wrongCounter * 100.0 / round), getString(R.string.including), missedCounter, getString(R.string.missed)) + '\n');       
        msgBuffer.append(getString(R.string.incompleted_summary) + String.format("%d (%.2f%%)", (round - roundCounter), ((round - roundCounter) * 100.0 / round)));
        AlertHelper.createAlertDialog(Gogogo.this, getString(R.string.summary), msgBuffer.toString(), mOnDialogClickListener).show();
        sync();
    }
    
    private String formatMode(String value) {
        if (MT.VALUE_MODE_EASY.equals(value)) {
            return getString(R.string.easy);
        } else if (MT.VALUE_MODE_NORMAL.equals(value)) {
            return getString(R.string.normal);
        } else if (MT.VALUE_MODE_HARD.equals(value)) {
            return getString(R.string.hard);
        } else if (MT.VALUE_MODE_CUSTOM.equals(value)) {
            return getString(R.string.custom);
        } else if (MT.VALUE_MODE_STUDY.equals(value)) {
            return getString(R.string.study);
        } else {
            return getString(R.string.unknown);
        }
    }
    
    private Score right = new Score(Score.SCORE_RIGHT);
    private Score wrong = new Score(Score.SCORE_WRONG);
    private Score missed = new Score(Score.SCORE_MISSED);
    private Runnable refresh = new Runnable() {
		
		@Override
		public void run() {
			if (mCellAdapter != null) {
				mCellAdapter.notifyDataSetChanged();
			}
		}
	};
	private Runnable goNext = new Runnable() {
        
        @Override
        public void run() {
            mInfoPanel.setVisibility(View.GONE);
            go();
        }
    };
    
    private class Runner {
        private final Puzzle puzzle;
        private final Runnable game;
        private boolean userChoice;
        
        private Runner(Puzzle puz) {
            puzzle = puz;
            game = new Runnable() {
                
                @Override
                public void run() {
                    if (!runningFlag) {
                        return;
                    }
                	mCellAdapter.setCells(puzzle.getBoard());
                	mCellAdapter.notifyDataSetChanged();
                    mZygote.postDelayed(missed, timeout);
                }
            };
        }
        
        void setUserChoice(boolean choice) {
            this.userChoice = choice;
        }
    }
    
    private boolean checkParam() {
        Bundle b = getIntent().getExtras();
        if (b == null) {
            return false;
        }
        mode = b.getString(MT.KEY_MODE);
        isStudyMode = MT.VALUE_MODE_STUDY.equals(mode);
        timeout = isStudyMode ? Integer.MAX_VALUE : b.getInt(MT.KEY_TIMEOUT, 2000);
        round = b.getInt(MT.KEY_ROUND, 30);
        condition = b.getInt(MT.KEY_GAMEOVERCONDITION, 40);
        isEnhanced = b.getBoolean(MT.KEY_EMHANCEDMODE, false);
        isPauseOnError = isStudyMode ? false : b.getBoolean(MT.KEY_PAUSEONERROR, false);
        isMarkIt = b.getBoolean(MT.KEY_MARKIT, false);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.game);
        mZygote = new Handler();
        checkParam();
        initCounter();
        initViews();
        runningFlag = true;
        go();
    }
    
    @Override
    protected void onDestroy() {
        runningFlag = false;
        super.onDestroy();
    }
    
    private void initCounter() {
        num1RightCounter = 0;
        num2RightCounter = 0;
        num3RightCounter = 0;
        num4RightCounter = 0;
        num5RightCounter = 0;
        num6RightCounter = 0;
        num7RightCounter = 0;
        num8RightCounter = 0;
        solvedCounter = 0;
    }
    
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (!isSolving) {
				return;
			}
			if (mCellAdapter.states[position] == MT.RESID_CLOSED) {
				mCellAdapter.states[position] = MT.RESID_SAFETY;
			} else if (mCellAdapter.states[position] == MT.RESID_SAFETY) {
				mCellAdapter.states[position] = MT.RESID_CLOSED;
			}
			if (mCellAdapter.states[position] == MT.RESID_CLOSED || mCellAdapter.states[position] == MT.RESID_SAFETY) {
				mCellAdapter.notifyDataSetChanged();
			}
		}
	};

    private void initViews() {
    	scale = 1.0f * getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160;
    	mGrid = (GridView) findViewById(R.id.grid);
    	mCellAdapter = new CellAdapter(new byte[0]);
		mGrid.setAdapter(mCellAdapter);
		mGrid.setOnItemClickListener(mOnItemClickListener);
		mGrid.setNumColumns(MT.BOARD_EDGE_WITH_WALL_SIZE);
		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
				(int) (MT.BOARD_EDGE_WITH_WALL_SIZE * 32 * scale),
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rl.addRule(RelativeLayout.CENTER_IN_PARENT);
		mGrid.setLayoutParams(rl);
		mGrid.requestLayout();
		
		mProgressBar = (ProgressBar) findViewById(R.id.progress);
		mProgressBar.setMax(round);
		
		mRight = (ImageButton) findViewById(R.id.right);
		mRight.setOnClickListener(mOnClickListener);
		mWrong = (ImageButton) findViewById(R.id.wrong);
		mWrong.setOnClickListener(mOnClickListener);
		mNext = (Button) findViewById(R.id.next);
		mNext.setOnClickListener(mOnClickListener);
		mNext.setVisibility(View.INVISIBLE);
		
		mInfoPanel = findViewById(R.id.debug);
		mInfoMode = (TextView) findViewById(R.id.mode_info);
	    mInfoRounds = (TextView) findViewById(R.id.round_info);
	    mInfoEnhanced = (TextView) findViewById(R.id.enhanced_info);
	    mInfoMark = (TextView) findViewById(R.id.mark_info);
	    mInfoCurrentRound = (TextView) findViewById(R.id.current_info);
	    mInfoCenterSolved = (TextView) findViewById(R.id.centersolv_info);
	    mInfoBoardSolved = (TextView) findViewById(R.id.boardsolv_info);
	    mInfoUnsolvedBlks = (TextView) findViewById(R.id.unsolvedblks_info);
	    mInfoUserChoice = (TextView) findViewById(R.id.userchoice_info);
	    mInfoUserMark = (TextView) findViewById(R.id.usermark_info);
	    mInfoCorrect = (TextView) findViewById(R.id.right_info);
	    mInfoWrong = (TextView) findViewById(R.id.wrong_info);
	    mInfoMissed = (TextView) findViewById(R.id.missed_info);
	    mInfoPanel.setVisibility(View.GONE);
	    
	    mInfoMode.setText("" + mode);
	    mInfoRounds.setText(String.valueOf(round));
	    mInfoEnhanced.setText(String.valueOf(isEnhanced));
	    mInfoMark.setText(String.valueOf(isMarkIt));
	    
	    mToggleButton = (ToggleButton) findViewById(android.R.id.toggle);
	    mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mNext != null && TAG_STUDY.equals(mNext.getTag()) && mNext.isShown()) {
                    if (mInfoPanel != null) {
                        mInfoPanel.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    }
                }
            }
        });
        mToggleButton.setVisibility(isStudyMode ? View.VISIBLE : View.GONE);
    }
    
    private final String TAG_PAUSE = "Pause";
    private final String TAG_SOLVE = "Solve";
    private final String TAG_STUDY = "Study";
    
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v == mNext) {
				Object tag = v.getTag();
				boolean studying = false;
				boolean solving = false;
				if (tag instanceof String) {
					solving = TAG_SOLVE.equals(tag);
					studying = TAG_STUDY.equals(tag);
				}
				mNext.setEnabled(false);
				mNext.setVisibility(View.INVISIBLE);
				isSolving = false;
				if (studying) {
				    mZygote.post(goNext);
				} else if (solving) {
					ArrayList<Integer> markedList = new ArrayList<Integer>();
					ArrayList<Integer> tmpMarkedList = new ArrayList<Integer>();
					for (int i = 0; i < mCellAdapter.states.length; i++) {
						if (mCellAdapter.states[i] == MT.RESID_SAFETY) {
							markedList.add(i);
						}
					}
					tmpMarkedList.addAll(markedList);
					boolean isMarkedRight = false;
					if (markedList.size() == currentRunner.puzzle.getClosedSafetyBlkPositions().size()) {
						markedList.removeAll(currentRunner.puzzle.getClosedSafetyBlkPositions());
						isMarkedRight = markedList.size() == 0;
					}
					if (!isStudyMode && isMarkedRight) {
						solvedCounter++;
						mZygote.post(right);
					} else {
						if (isPauseOnError && mNext != null) {
							mNext.setEnabled(true);
							mNext.setText(R.string.next);
							mNext.setTag(TAG_PAUSE);
							mNext.setVisibility(View.VISIBLE);
							mCellAdapter.setCells(currentRunner.puzzle.getBoard());
							for (Integer pos : tmpMarkedList) {
							    mCellAdapter.states[pos] = MT.RESID_SAFETY;
							}
							mZygote.post(refresh);
						} else {
						    if (isStudyMode) {
						        mCellAdapter.setCells(currentRunner.puzzle.getBoard());
						        for (Integer pos : tmpMarkedList) {
						            mCellAdapter.states[pos] = MT.RESID_SAFETY;
						        }
						        mZygote.post(refresh);
						    }
							mZygote.post(isMarkedRight ? right : wrong);
						}
					}
				} else {
					mZygote.post(wrong);
				}
				return;
			}
			if (currentRunner != null) {
			    setButtonState(false);
				mZygote.removeCallbacks(missed);
				boolean chooseRight = v == mRight;
				currentRunner.setUserChoice(chooseRight);
				final boolean result = isEnhanced ? (currentRunner.puzzle.isBoardClear() == chooseRight) : (currentRunner.puzzle.isCenterClear() == chooseRight);
				if (isPauseOnError && !result && mNext != null) {
					mNext.setEnabled(true);
					mNext.setText(R.string.next);
					mNext.setTag(TAG_PAUSE);
					mNext.setVisibility(View.VISIBLE);
				} else {
					if (result) {
						if (isEnhanced && isMarkIt && !chooseRight) {
							mNext.setEnabled(true);
							mNext.setText(R.string.next2);
							mNext.setTag(TAG_SOLVE);
							mNext.setVisibility(View.VISIBLE);
							isSolving = true;
							for (int i = 0; i < mCellAdapter.states.length; i++) {
								int status = mCellAdapter.states[i];
								if (status >= MT.CONTENT_NUM_1 && status <= MT.CONTENT_NUM_8) {
									mCellAdapter.states[i] = MT.CONTENT_EMPTY;
								}
							}
							mZygote.post(refresh);
						} else {
							increaseCenterRightCounter(currentRunner.puzzle.getCenterNumber());
							mZygote.post(right);
						}
					} else {
						mZygote.post(wrong);
					}
				}
			}
		}
	};
	
	private void setButtonState(boolean enable) {
	    if (mRight != null) {
	        mRight.setEnabled(enable);
	    }
	    if (mWrong != null) {
	        mWrong.setEnabled(enable);
	    }
	}
    
    private DialogInterface.OnClickListener mOnDialogClickListener = new DialogInterface.OnClickListener() {
        
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };
    
    private void sync() {
        if (isStudyMode) { // 学习模式不统计
            return;
        }
        Egg egg = Tmp.getTmpEgg();
        if (egg != null) {
            egg.COUNTER_FOR_NUMBER1_AND_RIGHT += num1RightCounter;
            egg.COUNTER_FOR_NUMBER2_AND_RIGHT += num2RightCounter;
            egg.COUNTER_FOR_NUMBER3_AND_RIGHT += num3RightCounter;
            egg.COUNTER_FOR_NUMBER4_AND_RIGHT += num4RightCounter;
            egg.COUNTER_FOR_NUMBER5_AND_RIGHT += num5RightCounter;
            egg.COUNTER_FOR_NUMBER6_AND_RIGHT += num6RightCounter;
            egg.COUNTER_FOR_NUMBER7_AND_RIGHT += num7RightCounter;
            egg.COUNTER_FOR_NUMBER8_AND_RIGHT += num8RightCounter;
            egg.COUNTER_FOR_SOLVED += solvedCounter;
            if (round == roundCounter) { // 完成度100%
                if (MT.VALUE_MODE_CUSTOM.equals(mode)) {
                    egg.COUNTER_FOR_CUSTOMMODE++;
                }
                if (rightCounter == roundCounter) { // 正确率100%
                	if (round >= 100 && !MT.VALUE_MODE_STUDY.equals(mode)) {
                		egg.COUNTER_FOR_COMP_AND_OVER100++;
                	}
                    if (MT.VALUE_MODE_EASY.equals(mode)) {
                        if (round >= 30 && !egg.isPractitioner() && !egg.isExpert()) {
                            egg.COUNTER_FOR_EASYMODE_COMP_AND_OVER30++;
                        }
                    } else if (MT.VALUE_MODE_NORMAL.equals(mode)) {
                        if (round >= 40 && !egg.isExpert()) {
                            egg.COUNTER_FOR_NORMALMODE_COMP_AND_OVER40++;
                        }
                    } else if (MT.VALUE_MODE_HARD.equals(mode)) {
                        if (round >= 50) {
                            egg.COUNTER_FOR_HARDMODE_COMP_AND_OVER50++;
                        }
                    } 
                }
            } else {
                egg.COUNTER_FOR_CONSECUTIVE_FAILURE++;
            }
            try {
                Tmp.sync(Gogogo.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private Runner currentRunner = null;
    
    private void go() {
        final Puzzle puz = MT.getPuzzle();
        if (puz == null) {
            AlertHelper.createAlertDialog(Gogogo.this , getString(R.string.error), getString(R.string.unknown_error), mOnDialogClickListener);
        } else {
            setButtonState(true);
        	currentRunner = new Runner(puz);
            mZygote.post(currentRunner.game);
        }
    }

    class CellAdapter extends BaseAdapter {

        private byte[] states = null;

        public CellAdapter(byte[] cells) {
            super();
            setCells(cells);
        }
        
        public void setCells(byte[] cells) {
            if (cells == null) {
                return;
            }
            states = new byte[cells.length];
            System.arraycopy(cells, 0, states, 0, cells.length);
        }

        @Override
        public int getCount() {
            return states.length;
        }

        @Override
        public Object getItem(int position) {
            return states[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView view = null;
            if (!(convertView instanceof ImageView)) {
                view = new ImageView(Gogogo.this);
            } else {
                view = (ImageView) convertView;
            }
            view.setImageResource(res[states[position]]);
            convertView = view;
            return convertView;
        }

    }
    
    void debug(String msg) {
        Log.d(TAG, msg);
    }
}
