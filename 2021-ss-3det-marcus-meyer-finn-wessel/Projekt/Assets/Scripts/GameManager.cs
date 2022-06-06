using System;
using System.IO;
using DefaultNamespace;
using UnityEngine;
using UnityEngine.Events;
using UnityEngine.SceneManagement;

public class GameManager : MonoBehaviour {
    private static GameManager _instance = null;
    public static GameManager Instance => _instance;

    private CameraMode _cameraMode = CameraMode.FREE;
    private DateTime _startTime;
    private TimeSpan[] _clearTime = new TimeSpan[3];
    private bool _isPaused = false;
    private bool _failure = false;
    private bool _success = false;
    private bool _hud = true;
    private bool _objIsFound = false;
    private float _lifeCounter = 1f;
    private float _shieldCounter = 0f;
    private int _objectiveCounter = 0;
    private static int _nextScene = 1;
    private String _failureReason;
    public String FailureReason => _failureReason;
    
    public CameraMode CameraMode => _cameraMode;
    
    public TimeSpan[] ClearTime => _clearTime;
    
    public readonly UnityEvent PauseGameEvent = new UnityEvent();
    public bool IsPaused => _isPaused;
    
    public readonly UnityEvent FailureEvent = new UnityEvent();
    public bool Failure => _failure;
    
    public readonly UnityEvent SuccessEvent = new UnityEvent();
    public bool Success => _success;
    
    public readonly UnityEvent HudEvent = new UnityEvent();
    public bool Hud => _hud;
    public bool ObjIsFound => _objIsFound;

    public readonly UnityEvent CollObjEvent = new UnityEvent();
    public int ObjectiveCounter => _objectiveCounter;
    
    public readonly UnityEvent LifeEvent = new UnityEvent();
    public float LifeCounter => _lifeCounter;
    public float ShieldCounter => _shieldCounter;


    public static void Load() {
        _nextScene = SceneManager.GetActiveScene().buildIndex;
        SceneManager.LoadScene(0);
    }
    public void Awake() {
        if (_instance == null) {
            _instance = this;
            DontDestroyOnLoad(gameObject);   
        }
        SceneManager.LoadScene(_nextScene);
    }

    public void Pause() {
        _isPaused = !_isPaused;
        if (_isPaused) {
            Time.timeScale = 0;
            _hud = false;
            Cursor.lockState = CursorLockMode.None;
        } else {
            Time.timeScale = 1;
            _hud = true;
            Cursor.lockState = CursorLockMode.Locked;
        }
        PauseGameEvent.Invoke();
        HudEvent.Invoke();
    }

    public void Lost(String reason) {
        _failureReason = reason;
        _failure = true;
        _hud = false;
        Time.timeScale = 0;
        Cursor.lockState = CursorLockMode.None;
        FailureEvent.Invoke();
        HudEvent.Invoke();
    }

    public void Won() {
        _success = true;
        _hud = false;
        Time.timeScale = 0;
        Cursor.lockState = CursorLockMode.None;
        _clearTime[SceneManager.GetActiveScene().buildIndex - 2] = DateTime.Now - _startTime;
        SuccessEvent.Invoke();
        HudEvent.Invoke();
    }

    private void ObjFound() {
        _objIsFound = true;
        HudEvent.Invoke();
    }
    
    public void IncObj() {
        _objectiveCounter++;
        ObjFound();
        CollObjEvent.Invoke();
    }

    public void DecLife(float value) {
        if (_shieldCounter > 0) {
            _shieldCounter -= value;
            if (_shieldCounter < 0) {
                _lifeCounter -= _shieldCounter;
            }
        } else {
            _lifeCounter -= value;
        }
        if (_lifeCounter <= 0) {
            Lost("YOU DIED");
        }
        LifeEvent.Invoke();
    }

    public void AddShield() {
        _shieldCounter = 0.5f;
        LifeEvent.Invoke();
    }

    public void SwitchCameraMode() {
        if ((int) _cameraMode >= Enum.GetNames(typeof(CameraMode)).Length - 1) {
            _cameraMode = 0;
        } else {
            _cameraMode++; 
        }
    }

    public void ToMenu() {
        resetScene();
        SceneManager.LoadScene(1);
    }

    private void resetScene() {
        _lifeCounter = 1f;
        _shieldCounter = 0f;
        _objectiveCounter = 0;
        _hud = true;
        _startTime = DateTime.Now;
        _isPaused = false;
        Time.timeScale = 1;
        Cursor.lockState = CursorLockMode.Locked;
    }
    
    public void StartGame() {
        resetScene();
        SceneManager.LoadScene(2);
    }

    public void ReloadScene() {
        resetScene();
        SceneManager.LoadScene(SceneManager.GetActiveScene().buildIndex);
    }

    public void LoadNextScene() {
        resetScene();
        Time.timeScale = 1;
        SceneManager.LoadScene(SceneManager.GetActiveScene().buildIndex + 1);
    }
}
