package org.apache.maven.mercury.repository.remote.m2;

import org.apache.maven.mercury.repository.api.AbstractRepository;
import org.apache.maven.mercury.repository.api.LocalRepository;
import org.apache.maven.mercury.repository.api.RemoteRepository;
import org.apache.maven.mercury.repository.api.Repository;
import org.apache.maven.mercury.repository.api.RepositoryException;
import org.apache.maven.mercury.repository.api.RepositoryWriter;
import org.apache.maven.mercury.repository.api.RepositoryWriterFactory;
import org.codehaus.plexus.lang.DefaultLanguage;
import org.codehaus.plexus.lang.Language;

public class RemoteRepositoryWriterM2Factory
implements RepositoryWriterFactory
{
  private static final Language lang = new DefaultLanguage( RemoteRepositoryWriterM2Factory.class );
  private static final RemoteRepositoryWriterM2Factory factory = new RemoteRepositoryWriterM2Factory();
  
  static 
  {
    AbstractRepository.register( AbstractRepository.DEFAULT_REPOSITORY_TYPE, factory  );
  }
  
  public RepositoryWriter getWriter( Repository repo )
  throws RepositoryException
  {
    if( repo == null || !(repo instanceof LocalRepository) )
      throw new RepositoryException( lang.getMessage( "bad.repository.type", repo == null ? "null" : repo.getClass().getName() ) );
    
    return new RemoteRepositoryWriterM2( (RemoteRepository)repo );
  }

}