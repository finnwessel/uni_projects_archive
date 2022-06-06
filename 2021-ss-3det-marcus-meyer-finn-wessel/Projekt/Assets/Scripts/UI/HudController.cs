using System;
using System.Collections;
using UnityEngine;
using UnityEngine.UI;

public class HudController : MonoBehaviour {
    private Canvas _hud;
    private Canvas _collObj;
    private Text _collObjText;
    private Image _healthAmount;
    private Image _shieldAmount;
    private Sprite _lowHealth;
    
    
    private Text _toastText;
    private float _toastTimer;
    
    void Start() {
        _hud = gameObject.GetComponent<Canvas>();
        _collObj = GameObject.Find("CollObj").GetComponent<Canvas>();
        _collObjText = GameObject.Find("CollObj/Number").GetComponent<Text>();
        _healthAmount = GameObject.Find("Healthbar/Health").GetComponent<Image>();
        _shieldAmount = GameObject.Find("Healthbar/Shield").GetComponent<Image>();
        _lowHealth = Resources.Load<Sprite>("HUD/hb_fg_2");
        _toastText = GameObject.Find("Toast").GetComponent<Text>();
        GameManager.Instance.CollObjEvent.AddListener(UpdateCollObjText);
        GameManager.Instance.LifeEvent.AddListener(UpdateLife);
        GameManager.Instance.HudEvent.AddListener(Show);
        _collObj.enabled = false;
        _toastText.enabled = false;
    }
    
    public void Show() {
        _hud.enabled = GameManager.Instance.Hud;
        _collObj.enabled = GameManager.Instance.ObjIsFound;
    }

    private void UpdateCollObjText() {
        _collObjText.text = GameManager.Instance.ObjectiveCounter.ToString();
    }

    private void UpdateLife() {
        _healthAmount.fillAmount = GameManager.Instance.LifeCounter;
        _shieldAmount.fillAmount = GameManager.Instance.ShieldCounter * 2;
        if (_healthAmount.fillAmount <= 0.3f && _healthAmount.sprite != _lowHealth) {
            _healthAmount.sprite = _lowHealth;
        }
    }
    
    public void ShowToast(String text, float time) {
        StartCoroutine(showToast(text, time));
    }

    private IEnumerator showToast(String text, float time) {
        _toastTimer = time;
        _toastText.text = text;
        if (!_toastText.enabled) {
            _toastText.enabled = true;
            while (_toastText.enabled) {
                if (_toastTimer >= 0) {
                    float alpha = Mathf.Lerp(0f, 1f, _toastTimer * 4);
                    _toastText.color = new Color(255, 255, 255, alpha);
                    _toastTimer -= Time.deltaTime;
                }
                else {
                    _toastText.enabled = false;
                }
                yield return new WaitForEndOfFrame();
            }
        }
    }
    
}
