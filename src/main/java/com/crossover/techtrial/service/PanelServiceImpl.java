package com.crossover.techtrial.service;

import com.crossover.techtrial.model.Panel;
import com.crossover.techtrial.repository.PanelRepository;

import java.time.LocalDateTime;


/**
 * PanelServiceImpl for panel related handling.
 * @author Crossover
 *
 */
public class PanelServiceImpl implements PanelService {

  private final PanelRepository panelRepository;

  public PanelServiceImpl(final PanelRepository panelRepository) {
    this.panelRepository = panelRepository;
  }

  /* (non-Javadoc)
   * @see com.crossover.techtrial.service.PanelService#register(com.crossover.techtrial.model.Panel)
   */

  @Override
  public void register(Panel panel) {
    panel.setCreatedAt(LocalDateTime.now());
    panelRepository.save(panel);
  }
  
  public Panel findBySerial(String serial) {
    return panelRepository.findBySerial(serial);
  }
}
