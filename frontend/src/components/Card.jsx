export default function Card({ title, subtitle, children }) {
  return (
    <div className="card">
      {title ? <h4>{title}</h4> : null}
      {subtitle ? <p className="muted">{subtitle}</p> : null}
      {children}
    </div>
  );
}
