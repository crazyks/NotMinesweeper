package com.crazyks.mt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class NotMinesweeper extends Activity {
    private final String TAG = "MT";
    
    private RadioGroup mMode = null;
    private RadioButton mEasy = null;
    private RadioButton mNormal = null;
    private RadioButton mHard = null;
    private RadioButton mCustom = null;
    private RadioButton mStudy = null;
    private EditText mTimeout = null;
    private EditText mRound = null;
    private EditText mEnd = null;
    private CheckBox mEnhancedMode = null;
    private CheckBox mPauseOnError = null;
    private CheckBox mMarkIt = null;
    private Button mGo = null;
    private Button mHelp = null;
    private Button mAchv = null;
    private Button mExit = null;
    private TextView mVersion = null;
    
    private Toast mToast = null;
    
    private final int CHECK_SUCCESS = 0;
    private final int CHECK_TIMEOUT_CFG_WRONG = 1;
    private final int CHECK_ROUND_CFG_WRONG = 2;
    private final int CHECK_QUIT_CFG_WRONG = 3;
    private final int CHECK_UNKNOWN_ERROR = 4;
    
    private LaunchParam savedParam = null;
    
    private class LaunchParam {
        private String mode;
        private int timeout;
        private int round;
        private int gameOverCondition;
        private boolean isEnhanced;
        private boolean pauseOnError;
        private boolean markIt;
        
        private LaunchParam() {
        }
        
        private LaunchParam(String mode, int timeout, int round, int condition, boolean isEnhanced, boolean pauseOnError, boolean markit) {
            this.mode = mode;
            this.timeout = timeout;
            this.round = round;
            this.gameOverCondition = condition;
            this.isEnhanced = isEnhanced;
            this.pauseOnError = pauseOnError;
            this.markIt = markit;
        }

		@Override
		public boolean equals(Object o) {
			if (o instanceof LaunchParam) {
				LaunchParam other = (LaunchParam) o;
				if (other.mode == null || this.mode == null) {
					return false;
				}
				return (other.mode.equals(this.mode)
							&& other.timeout == this.timeout
							&& other.round == this.round
							&& other.gameOverCondition == this.gameOverCondition
							&& other.isEnhanced == this.isEnhanced
							&& other.pauseOnError == this.pauseOnError
							&& other.markIt == this.markIt);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}
    }
    
    private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
        
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (buttonView == mEasy) {
                    mTimeout.setText(String.valueOf(2000));
                    mTimeout.setEnabled(false);
                } else if (buttonView == mNormal) {
                    mTimeout.setText(String.valueOf(1200));
                    mTimeout.setEnabled(false);
                } else if (buttonView == mHard) {
                    mTimeout.setText(String.valueOf(800));
                    mTimeout.setEnabled(false);
                } else if (buttonView == mCustom) {
                    mTimeout.setEnabled(true);
                } else if (buttonView == mStudy) {
                    mTimeout.setEnabled(false);
                }
            }
            if (buttonView == mStudy) {
                if (mPauseOnError != null) {
                    mPauseOnError.setEnabled(!isChecked);
                }
                if (mEnd != null) {
                    mEnd.setEnabled(!isChecked);
                }
            }
        }
    };
    
    private OnClickListener mOnClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            if (v == mGo) {
                LaunchParam param = new LaunchParam();
                switch (checkConfig(param)) {
                case CHECK_SUCCESS:
                    save(param);
                    if (!launch(param)) {
                        toast(getString(R.string.unknown_error));
                    }
                    break;
                case CHECK_TIMEOUT_CFG_WRONG:
                    toast(getString(R.string.timeouttips) + ' ' + getString(R.string.timeouthint));
                    break;
                case  CHECK_ROUND_CFG_WRONG:
                    toast(getString(R.string.roundtips) + ' ' + getString(R.string.roundhint));
                    break;
                case  CHECK_QUIT_CFG_WRONG:
                    toast(getString(R.string.quittips) + ' ' + getString(R.string.quitafterhint));
                    break;
                default:
                    toast(getString(R.string.unknown_error));
                    break;
                }
                    
            } else if (v== mHelp) {
                showHelp();
            } else if (v== mAchv) {
            	showAchv();
            } else if (v == mExit) {
                finish();
            }
        }
    };
    
    private void showHelp() {
        AlertHelper.createAlertDialog(NotMinesweeper.this, getString(R.string.help), getString(R.string.help_info), new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        }).show();
    }
    
    private void showAchv() {
    	try {
    		startActivity(new Intent(NotMinesweeper.this, Achievements.class));
    	} catch (Exception e) {
		}
    }
    
    private int checkConfig(final LaunchParam param) {
    	if (param != null) {
    		if (mEasy.isChecked()) {
    			param.mode = MT.VALUE_MODE_EASY;
    		} else if (mNormal.isChecked()) {
    			param.mode = MT.VALUE_MODE_NORMAL;
    		} else if (mHard.isChecked()) {
    			param.mode = MT.VALUE_MODE_HARD;
    		} else if (mCustom.isChecked()) {
    			param.mode = MT.VALUE_MODE_CUSTOM;
    		} else if (mStudy.isChecked()) {
    		    param.mode = MT.VALUE_MODE_STUDY;
    		} else {
    			return CHECK_UNKNOWN_ERROR;
    		}
    		
    		Integer timeout = parseInt(mTimeout.getText().toString(), 100, 10000);
    		if (timeout == null) {
    			return CHECK_TIMEOUT_CFG_WRONG;
    		}
    		param.timeout = timeout.intValue();
    		
    		Integer round = parseInt(mRound.getText().toString(), 1, 9999999);
    		if (round == null) {
    			return CHECK_ROUND_CFG_WRONG;
    		}
    		param.round = round.intValue();
    		
    		Integer condition = parseInt(mEnd.getText().toString(), 0, 100);
    		if (condition == null) {
    			return CHECK_QUIT_CFG_WRONG;
    		}
    		param.gameOverCondition = condition.intValue();
    		
    		param.isEnhanced = mEnhancedMode.isChecked();
    		param.pauseOnError = mPauseOnError.isChecked();
    		param.markIt = mMarkIt.isChecked();
    		
    		return CHECK_SUCCESS;
    	}
        return CHECK_UNKNOWN_ERROR;
    }
    
    private Integer parseInt(String numberString, int min, int max) {
    	int res = min - 1;
    	String number = null;
    	try {
    		number = numberString.trim();
    		res = Integer.parseInt(number);
    	} catch (Exception e) {
    		res = min -1;
		}
    	if (res < min || res > max) {
    		return null;
    	} else {
    		return new Integer(res);
    	}
    }
    
    private boolean save(final LaunchParam param) {
        if (param == null) {
            return false;
        }
        if (param.equals(savedParam)) {
        	return true;
        } else {
        	savedParam = param;
        }
        Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putString(MT.KEY_MODE, param.mode);
        editor.putInt(MT.KEY_TIMEOUT, param.timeout);
        editor.putInt(MT.KEY_ROUND, param.round);
        editor.putInt(MT.KEY_GAMEOVERCONDITION, param.gameOverCondition);
        editor.putBoolean(MT.KEY_EMHANCEDMODE, param.isEnhanced);
        editor.putBoolean(MT.KEY_PAUSEONERROR, param.pauseOnError);
        editor.putBoolean(MT.KEY_MARKIT, param.markIt);
        return editor.commit();
    }
    
    private boolean launch(final LaunchParam param) {
        if (param == null) {
            return false;
        }
        boolean launchSuccess = true;
        try {
            Intent i = new Intent(NotMinesweeper.this, Gogogo.class);
            Bundle b = new Bundle();
            b.putString(MT.KEY_MODE, param.mode);
            b.putInt(MT.KEY_TIMEOUT, param.timeout);
            b.putInt(MT.KEY_ROUND, param.round);
            b.putInt(MT.KEY_GAMEOVERCONDITION, param.gameOverCondition);
            b.putBoolean(MT.KEY_EMHANCEDMODE, param.isEnhanced);
            b.putBoolean(MT.KEY_PAUSEONERROR, param.pauseOnError);
            b.putBoolean(MT.KEY_MARKIT, param.markIt);
            i.putExtras(b);
            startActivity(i);
        } catch (Exception e) {
            launchSuccess = false;
        }
        return launchSuccess;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, getString(R.string.about)).setIcon(android.R.drawable.ic_menu_info_details);  
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == Menu.FIRST + 1){ 
			AlertHelper.createAlertDialog(NotMinesweeper.this, getString(R.string.about), getString(R.string.about_info), new DialogInterface.OnClickListener() {
	            
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                // Do nothing
	            }
	        }).show();
			Egg egg = Tmp.getTmpEgg();
			if (!egg.isCurious()) {
    			egg.FLAG_FOR_FIND_EMAIL++;
    			try {
                    Tmp.sync(NotMinesweeper.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
			}
		}
		return super.onMenuItemSelected(featureId, item);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        init();
        check();
    }
    
    private void init() {
        initViews();
        initListener();
        initConfig();
    }

    private void initViews() {
        mMode = (RadioGroup) findViewById(R.id.level_config);
        mEasy = (RadioButton) findViewById(R.id.easy);
        mNormal = (RadioButton) findViewById(R.id.normal);
        mHard = (RadioButton) findViewById(R.id.hard);
        mCustom = (RadioButton) findViewById(R.id.custom);
        mStudy = (RadioButton) findViewById(R.id.study);
        mTimeout = (EditText) findViewById(R.id.timeout);
        mRound = (EditText) findViewById(R.id.round);
        mEnd = (EditText) findViewById(R.id.quit);
        mEnhancedMode = (CheckBox) findViewById(R.id.extra);
        mPauseOnError = (CheckBox) findViewById(R.id.pause);
        mMarkIt = (CheckBox) findViewById(R.id.mark);
        mGo = (Button) findViewById(R.id.go);
        mHelp = (Button) findViewById(R.id.help);
        mAchv = (Button) findViewById(R.id.achv);
        mExit = (Button) findViewById(R.id.exit);
        mVersion = (TextView) findViewById(R.id.version);
    }
    
    private void initListener() {
        mEasy.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mNormal.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mHard.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mCustom.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mStudy.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mEnhancedMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (buttonView == mEnhancedMode && mMarkIt != null) {
					mMarkIt.setEnabled(isChecked);
				}
			}
		});
        mGo.setOnClickListener(mOnClickListener);
        mHelp.setOnClickListener(mOnClickListener);
        mAchv.setOnClickListener(mOnClickListener);
        mExit.setOnClickListener(mOnClickListener);
    }
    
    private void initConfig() {
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        String mode = sp.getString(MT.KEY_MODE, MT.VALUE_MODE_EASY);
        int timeout = sp.getInt(MT.KEY_TIMEOUT, 2000);
        int rounds = sp.getInt(MT.KEY_ROUND, 30);
        int gameOverCondition = sp.getInt(MT.KEY_GAMEOVERCONDITION, 40);
        boolean isEnhancedModeOn = sp.getBoolean(MT.KEY_EMHANCEDMODE, false);
        boolean isPauseOnErrorOn = sp.getBoolean(MT.KEY_PAUSEONERROR, false);
        boolean markIt = sp.getBoolean(MT.KEY_MARKIT, false);
        savedParam = new LaunchParam(mode, timeout, rounds, gameOverCondition, isEnhancedModeOn, isPauseOnErrorOn, markIt);
        
        mMode.clearCheck();
        if (MT.VALUE_MODE_EASY.equals(mode)) {
            mEasy.setChecked(true);
        } else if (MT.VALUE_MODE_NORMAL.equals(mode)) {
            mNormal.setChecked(true);
        } else if (MT.VALUE_MODE_HARD.equals(mode)) {
            mHard.setChecked(true);
        } else if (MT.VALUE_MODE_CUSTOM.equals(mode)) {
            mCustom.setChecked(true);
        } else if (MT.VALUE_MODE_STUDY.equals(mode)) {
            mStudy.setChecked(true);
        } else {
            mode = MT.VALUE_MODE_EASY;
            mEasy.setChecked(true);
        }
        
        if (timeout < 100 || timeout > 10000) {
            timeout = 2000;
        }
        mTimeout.setText(String.valueOf(timeout));
        
        if (rounds < 1 || rounds > 9999999) {
            rounds = 30;
        }
        mRound.setText(String.valueOf(rounds));
        
        if (gameOverCondition < 0 || gameOverCondition > 100) {
            gameOverCondition = 40;
        }
        mEnd.setText(String.valueOf(gameOverCondition));
        
        mEnhancedMode.setChecked(isEnhancedModeOn);
        mPauseOnError.setChecked(isPauseOnErrorOn);
        mPauseOnError.setEnabled(!mStudy.isChecked());
        mMarkIt.setChecked(markIt);
    	mMarkIt.setEnabled(isEnhancedModeOn);
        
        mVersion.setText('V' + myVersion());
    }
    
    private void check() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            AlertHelper.createAlertDialog(NotMinesweeper.this, getString(R.string.notice), getString(R.string.no_sdcard), new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
            return;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == AlertDialog.BUTTON_POSITIVE) {
                    try {
                        Egg egg = new Egg();
                        Tmp.init(egg);
                        Egg.sync(egg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (which == AlertDialog.BUTTON_NEGATIVE){
                    finish();
                }
            }
        };
        if (Egg.save.exists()) {
            Egg egg = null;
            try {
                egg = Egg.load();
            } catch (Exception e) {
                e.printStackTrace();
                egg = null;
            }
            if (egg == null) {
                AlertHelper.createWarningDialog(NotMinesweeper.this, getString(R.string.notice), getString(R.string.file_damaged), listener).show();
            } else {
                SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
                String latestDate = sp.getString(MT.KEY_LATEST_PLAY_DATE, "1988-12-23");
                String now = DateHelper.now();
                try {
                    int diff = DateHelper.diffDate(now, latestDate);
                    if (diff != 0) {
                        sp.edit().putString(MT.KEY_LATEST_PLAY_DATE, now).commit();
                        if (diff == 1) {
                            egg.COUNTER_FOR_CONSECUTIVE_DAYS++;
                        } else {
                            egg.COUNTER_FOR_CONSECUTIVE_DAYS = 0;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Tmp.init(egg);
                try {
                    Tmp.sync(NotMinesweeper.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            AlertHelper.createWarningDialog(NotMinesweeper.this, getString(R.string.notice), getString(R.string.file_not_found), listener).show();
        }
    }
    
    private void toast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(NotMinesweeper.this, msg, Toast.LENGTH_SHORT);
        }
        mToast.cancel();
        mToast.setText(msg);
        mToast.show();
    }
    
    private String myVersion() {
        String version = null;
        try {
            PackageInfo packInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            version = packInfo.versionName;
        } catch (Exception e) {
            version = null;
        }
        return version;
    }
    
    void debug(String msg) {
        Log.d(TAG, msg);
    }
}