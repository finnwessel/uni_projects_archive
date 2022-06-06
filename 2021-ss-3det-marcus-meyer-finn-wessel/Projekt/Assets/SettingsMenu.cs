using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Audio;
public class SettingsMenu : MonoBehaviour
{
    public AudioMixer AudioMixer;
    
    public void SetMasterVolume(float volume)
    {
        AudioMixer.SetFloat("MasterVolume", volume);
    }
    public void SetMusicVolume(float volume)
    {
        AudioMixer.SetFloat("MusicVolume", volume);
    }
    public void SetEffectsVolume(float volume)
    {
        AudioMixer.SetFloat("EffectsVolume", volume);
    }
}
