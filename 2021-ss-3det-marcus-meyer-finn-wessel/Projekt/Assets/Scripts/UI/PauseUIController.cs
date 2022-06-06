using UnityEngine;

public class PauseUIController : MonoBehaviour {
    private Canvas _pauseUi;
    private GameObject _pauseMenu;
    private GameObject _settingsMenu;
    void Start()
    {
        _pauseMenu = transform.Find("PauseMenu").gameObject;
        _settingsMenu = transform.Find("SettingsMenu").gameObject;
        _pauseUi = gameObject.GetComponent<Canvas>();
        _pauseUi.enabled = false;
        GameManager.Instance.PauseGameEvent.AddListener(Show);
    }

    public void Show() {
        _pauseUi.enabled = GameManager.Instance.IsPaused;
        _pauseMenu.SetActive(true);
        _settingsMenu.SetActive(false);
    }

    public void ShowSettings()
    {
        _pauseMenu.SetActive(false);
        _settingsMenu.SetActive(true);
    }
    
    public void HideSettings()
    {
        _settingsMenu.SetActive(false);
        _pauseMenu.SetActive(true);
    }
    
    public void Restart() {
        GameManager.Instance.Pause();
        GameManager.Instance.ReloadScene();
    }

    public void Menu() {
        GameManager.Instance.ToMenu();
    }
}
